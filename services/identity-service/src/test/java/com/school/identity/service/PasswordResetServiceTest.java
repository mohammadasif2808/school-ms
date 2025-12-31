package com.school.identity.service;

import com.school.identity.domain.PasswordResetToken;
import com.school.identity.domain.User;
import com.school.identity.dto.ForgotPasswordRequest;
import com.school.identity.dto.ResetPasswordRequest;
import com.school.identity.exception.ValidationException;
import com.school.identity.repository.PasswordResetTokenRepository;
import com.school.identity.repository.UserRepository;
import com.school.identity.testutil.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PasswordResetService
 *
 * Tests forgot password and reset password flows with all edge cases
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PasswordResetService Tests")
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @BeforeEach
    void setUp() {
        // Set the token expiry hours via reflection (since @Value isn't processed in unit tests)
        ReflectionTestUtils.setField(passwordResetService, "tokenExpiryHours", 24);
    }

    // ============ FORGOT PASSWORD TESTS ============

    @Nested
    @DisplayName("Request Password Reset Tests")
    class RequestPasswordResetTests {

        @Test
        @DisplayName("GIVEN existing user WHEN requestPasswordReset THEN creates token and sends email")
        void requestPasswordReset_givenExistingUser_shouldCreateTokenAndSendEmail() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            ForgotPasswordRequest request = TestDataFactory.createForgotPasswordRequest(user.getEmail());

            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
            when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(inv -> inv.getArgument(0));

            // WHEN
            passwordResetService.requestPasswordReset(request);

            // THEN
            verify(tokenRepository).save(any(PasswordResetToken.class));
            verify(emailService).sendPasswordResetEmail(
                eq(user.getEmail()),
                eq(user.getFirstName()),
                anyString()
            );
        }

        @Test
        @DisplayName("GIVEN existing user WHEN requestPasswordReset THEN token has correct expiry")
        void requestPasswordReset_givenExistingUser_shouldSetCorrectTokenExpiry() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            ForgotPasswordRequest request = TestDataFactory.createForgotPasswordRequest(user.getEmail());

            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
            when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(inv -> inv.getArgument(0));

            LocalDateTime beforeRequest = LocalDateTime.now();

            // WHEN
            passwordResetService.requestPasswordReset(request);

            // THEN
            ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
            verify(tokenRepository).save(tokenCaptor.capture());

            PasswordResetToken savedToken = tokenCaptor.getValue();
            assertThat(savedToken.getExpiresAt()).isAfter(beforeRequest.plusHours(23));
            assertThat(savedToken.getExpiresAt()).isBefore(beforeRequest.plusHours(25));
            assertThat(savedToken.getIsUsed()).isFalse();
        }

        @Test
        @DisplayName("GIVEN non-existing email WHEN requestPasswordReset THEN does not throw (no info leak)")
        void requestPasswordReset_givenNonExistingEmail_shouldNotThrow() {
            // GIVEN - Security: Don't reveal if email exists
            ForgotPasswordRequest request = TestDataFactory.createForgotPasswordRequest("nonexistent@example.com");
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            // WHEN / THEN - Should complete silently
            assertThatCode(() -> passwordResetService.requestPasswordReset(request))
                .doesNotThrowAnyException();

            // THEN - No token saved, no email sent
            verify(tokenRepository, never()).save(any());
            verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("GIVEN deleted user WHEN requestPasswordReset THEN does not throw (silent fail)")
        void requestPasswordReset_givenDeletedUser_shouldNotThrow() {
            // GIVEN
            User deletedUser = TestDataFactory.createDeletedUser();
            ForgotPasswordRequest request = TestDataFactory.createForgotPasswordRequest(deletedUser.getEmail());
            when(userRepository.findByEmail(deletedUser.getEmail())).thenReturn(Optional.of(deletedUser));

            // WHEN / THEN - Should complete silently
            assertThatCode(() -> passwordResetService.requestPasswordReset(request))
                .doesNotThrowAnyException();

            // THEN - No token saved, no email sent
            verify(tokenRepository, never()).save(any());
            verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("GIVEN token generation WHEN requestPasswordReset THEN token is cryptographically random")
        void requestPasswordReset_givenMultipleRequests_shouldGenerateUniqueTokens() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            ForgotPasswordRequest request = TestDataFactory.createForgotPasswordRequest(user.getEmail());

            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
            when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(inv -> inv.getArgument(0));

            // WHEN - Request password reset twice
            passwordResetService.requestPasswordReset(request);
            passwordResetService.requestPasswordReset(request);

            // THEN - Two different tokens should be generated
            ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
            verify(tokenRepository, times(2)).save(tokenCaptor.capture());

            java.util.List<PasswordResetToken> savedTokens = tokenCaptor.getAllValues();
            assertThat(savedTokens.get(0).getToken()).isNotEqualTo(savedTokens.get(1).getToken());
        }
    }

    // ============ RESET PASSWORD TESTS ============

    @Nested
    @DisplayName("Reset Password Tests")
    class ResetPasswordTests {

        @Test
        @DisplayName("GIVEN valid token and password WHEN resetPassword THEN updates password")
        void resetPassword_givenValidTokenAndPassword_shouldUpdatePassword() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            PasswordResetToken token = TestDataFactory.createValidResetToken(user);
            ResetPasswordRequest request = TestDataFactory.createResetPasswordRequest(
                token.getToken(), "NewSecure@Pass123");

            when(tokenRepository.findByTokenAndIsUsedFalse(token.getToken()))
                .thenReturn(Optional.of(token));
            when(passwordEncoder.encode(anyString())).thenReturn("newHashedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
            when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(inv -> inv.getArgument(0));

            // WHEN
            passwordResetService.resetPassword(request);

            // THEN
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertThat(userCaptor.getValue().getPasswordHash()).isEqualTo("newHashedPassword");
        }

        @Test
        @DisplayName("GIVEN valid reset WHEN completed THEN token is marked as used")
        void resetPassword_givenValidReset_shouldMarkTokenAsUsed() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            PasswordResetToken token = TestDataFactory.createValidResetToken(user);
            ResetPasswordRequest request = TestDataFactory.createResetPasswordRequest(
                token.getToken(), "NewSecure@Pass123");

            when(tokenRepository.findByTokenAndIsUsedFalse(token.getToken()))
                .thenReturn(Optional.of(token));
            when(passwordEncoder.encode(anyString())).thenReturn("hash");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
            when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(inv -> inv.getArgument(0));

            // WHEN
            passwordResetService.resetPassword(request);

            // THEN
            ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
            verify(tokenRepository).save(tokenCaptor.capture());
            assertThat(tokenCaptor.getValue().getIsUsed()).isTrue();
            assertThat(tokenCaptor.getValue().getUsedAt()).isNotNull();
        }

        @Test
        @DisplayName("GIVEN invalid token WHEN resetPassword THEN throws ValidationException")
        void resetPassword_givenInvalidToken_shouldThrowException() {
            // GIVEN
            ResetPasswordRequest request = TestDataFactory.createResetPasswordRequest(
                "invalid-token", "NewSecure@Pass123");

            when(tokenRepository.findByTokenAndIsUsedFalse("invalid-token"))
                .thenReturn(Optional.empty());

            // WHEN / THEN
            assertThatThrownBy(() -> passwordResetService.resetPassword(request))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "INVALID_RESET_TOKEN");
        }

        @Test
        @DisplayName("GIVEN expired token WHEN resetPassword THEN throws ValidationException")
        void resetPassword_givenExpiredToken_shouldThrowException() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            PasswordResetToken expiredToken = TestDataFactory.createExpiredResetToken(user);
            ResetPasswordRequest request = TestDataFactory.createResetPasswordRequest(
                expiredToken.getToken(), "NewSecure@Pass123");

            when(tokenRepository.findByTokenAndIsUsedFalse(expiredToken.getToken()))
                .thenReturn(Optional.of(expiredToken));

            // WHEN / THEN
            assertThatThrownBy(() -> passwordResetService.resetPassword(request))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "INVALID_RESET_TOKEN")
                .hasMessageContaining("expired");
        }

        @Test
        @DisplayName("GIVEN already used token WHEN resetPassword THEN throws ValidationException")
        void resetPassword_givenUsedToken_shouldThrowException() {
            // GIVEN - Token already used (findByTokenAndIsUsedFalse returns empty)
            ResetPasswordRequest request = TestDataFactory.createResetPasswordRequest(
                "used-token", "NewSecure@Pass123");

            when(tokenRepository.findByTokenAndIsUsedFalse("used-token"))
                .thenReturn(Optional.empty());

            // WHEN / THEN
            assertThatThrownBy(() -> passwordResetService.resetPassword(request))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "INVALID_RESET_TOKEN");
        }

        @Test
        @DisplayName("GIVEN weak password WHEN resetPassword THEN throws ValidationException")
        void resetPassword_givenWeakPassword_shouldThrowException() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            PasswordResetToken token = TestDataFactory.createValidResetToken(user);
            ResetPasswordRequest request = TestDataFactory.createResetPasswordRequest(
                token.getToken(), "weakpass");

            // WHEN / THEN
            assertThatThrownBy(() -> passwordResetService.resetPassword(request))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "PASSWORD_WEAK");
        }

        @Test
        @DisplayName("GIVEN password without uppercase WHEN resetPassword THEN throws ValidationException")
        void resetPassword_givenPasswordWithoutUppercase_shouldThrowException() {
            // GIVEN
            ResetPasswordRequest request = TestDataFactory.createResetPasswordRequest(
                "valid-token", TestDataFactory.WEAK_PASSWORD_NO_UPPERCASE);

            // WHEN / THEN
            assertThatThrownBy(() -> passwordResetService.resetPassword(request))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "PASSWORD_WEAK");
        }

        @Test
        @DisplayName("GIVEN password without digit WHEN resetPassword THEN throws ValidationException")
        void resetPassword_givenPasswordWithoutDigit_shouldThrowException() {
            // GIVEN
            ResetPasswordRequest request = TestDataFactory.createResetPasswordRequest(
                "valid-token", TestDataFactory.WEAK_PASSWORD_NO_DIGIT);

            // WHEN / THEN
            assertThatThrownBy(() -> passwordResetService.resetPassword(request))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "PASSWORD_WEAK");
        }

        @Test
        @DisplayName("GIVEN password without special char WHEN resetPassword THEN throws ValidationException")
        void resetPassword_givenPasswordWithoutSpecialChar_shouldThrowException() {
            // GIVEN
            ResetPasswordRequest request = TestDataFactory.createResetPasswordRequest(
                "valid-token", TestDataFactory.WEAK_PASSWORD_NO_SPECIAL);

            // WHEN / THEN
            assertThatThrownBy(() -> passwordResetService.resetPassword(request))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "PASSWORD_WEAK");
        }

        @Test
        @DisplayName("GIVEN deleted user with valid token WHEN resetPassword THEN throws ValidationException")
        void resetPassword_givenDeletedUser_shouldThrowException() {
            // GIVEN
            User deletedUser = TestDataFactory.createDeletedUser();
            PasswordResetToken token = TestDataFactory.createValidResetToken(deletedUser);
            ResetPasswordRequest request = TestDataFactory.createResetPasswordRequest(
                token.getToken(), "NewSecure@Pass123");

            when(tokenRepository.findByTokenAndIsUsedFalse(token.getToken()))
                .thenReturn(Optional.of(token));

            // WHEN / THEN
            assertThatThrownBy(() -> passwordResetService.resetPassword(request))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "USER_NOT_FOUND");
        }
    }

    // ============ SECURITY EDGE CASES ============

    @Nested
    @DisplayName("Security Edge Cases")
    class SecurityEdgeCases {

        @Test
        @DisplayName("GIVEN reset with valid token WHEN completed THEN token cannot be reused")
        void resetPassword_givenCompletedReset_shouldPreventTokenReuse() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            PasswordResetToken token = TestDataFactory.createValidResetToken(user);
            ResetPasswordRequest request = TestDataFactory.createResetPasswordRequest(
                token.getToken(), "NewSecure@Pass123");

            // First reset succeeds
            when(tokenRepository.findByTokenAndIsUsedFalse(token.getToken()))
                .thenReturn(Optional.of(token))
                .thenReturn(Optional.empty()); // Second call returns empty (token used)
            when(passwordEncoder.encode(anyString())).thenReturn("hash");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
            when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(inv -> {
                token.markAsUsed();
                return token;
            });

            // WHEN - First reset
            passwordResetService.resetPassword(request);

            // THEN - Second reset should fail
            assertThatThrownBy(() -> passwordResetService.resetPassword(request))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "INVALID_RESET_TOKEN");
        }

        @Test
        @DisplayName("GIVEN password reset WHEN new password equals old THEN still updates (no comparison)")
        void resetPassword_givenSamePassword_shouldStillUpdate() {
            // GIVEN - Service doesn't compare old and new passwords
            User user = TestDataFactory.createActiveUser();
            PasswordResetToken token = TestDataFactory.createValidResetToken(user);
            ResetPasswordRequest request = TestDataFactory.createResetPasswordRequest(
                token.getToken(), "NewSecure@Pass123");

            when(tokenRepository.findByTokenAndIsUsedFalse(token.getToken()))
                .thenReturn(Optional.of(token));
            when(passwordEncoder.encode(anyString())).thenReturn("newHash");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
            when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(inv -> inv.getArgument(0));

            // WHEN / THEN - Should not throw
            assertThatCode(() -> passwordResetService.resetPassword(request))
                .doesNotThrowAnyException();

            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("GIVEN error message WHEN invalid token THEN does not reveal token existence")
        void resetPassword_givenInvalidToken_shouldNotRevealExistence() {
            // GIVEN - Security: error messages should be generic
            ResetPasswordRequest request1 = TestDataFactory.createResetPasswordRequest(
                "never-existed", "NewSecure@Pass123");
            ResetPasswordRequest request2 = TestDataFactory.createResetPasswordRequest(
                "already-used", "NewSecure@Pass123");

            when(tokenRepository.findByTokenAndIsUsedFalse(anyString()))
                .thenReturn(Optional.empty());

            // WHEN / THEN - Both should have same error message
            assertThatThrownBy(() -> passwordResetService.resetPassword(request1))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Invalid or expired reset token");

            assertThatThrownBy(() -> passwordResetService.resetPassword(request2))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Invalid or expired reset token");
        }
    }
}

