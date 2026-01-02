package com.school.identity.service;

import com.school.identity.domain.User;
import com.school.identity.domain.UserStatus;
import com.school.identity.dto.SignInRequest;
import com.school.identity.dto.SignUpRequest;
import com.school.identity.exception.AuthenticationException;
import com.school.identity.exception.ValidationException;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthenticationService
 *
 * Tests signup and signin business logic with all edge cases
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationService Tests")
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    // ============ SIGNUP TESTS ============

    @Nested
    @DisplayName("Sign Up Tests")
    class SignUpTests {

        @Test
        @DisplayName("GIVEN valid signup request WHEN signUp THEN user is created successfully")
        void signUp_givenValidRequest_shouldCreateUser() {
            // GIVEN
            SignUpRequest request = TestDataFactory.createValidSignUpRequest();

            when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
            when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(userRepository.count()).thenReturn(1L); // Ensure user is not super admin
            when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(java.util.UUID.randomUUID());
                return user;
            });

            // WHEN
            User result = authenticationService.signUp(request);

            // THEN
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo(request.getUsername());
            assertThat(result.getEmail()).isEqualTo(request.getEmail());
            assertThat(result.getFirstName()).isEqualTo(request.getFirst_name());
            assertThat(result.getLastName()).isEqualTo(request.getLast_name());
            assertThat(result.getPhone()).isEqualTo(request.getPhone());
            assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(result.getIsSuperAdmin()).isFalse();
            assertThat(result.getIsDeleted()).isFalse();

            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("GIVEN duplicate username WHEN signUp THEN throws AuthenticationException")
        void signUp_givenDuplicateUsername_shouldThrowException() {
            // GIVEN
            SignUpRequest request = TestDataFactory.createValidSignUpRequest();
            when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

            // WHEN / THEN
            assertThatThrownBy(() -> authenticationService.signUp(request))
                .isInstanceOf(AuthenticationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "USERNAME_EXISTS")
                .hasMessageContaining("Username already exists");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("GIVEN duplicate email WHEN signUp THEN throws AuthenticationException")
        void signUp_givenDuplicateEmail_shouldThrowException() {
            // GIVEN
            SignUpRequest request = TestDataFactory.createValidSignUpRequest();
            when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
            when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

            // WHEN / THEN
            assertThatThrownBy(() -> authenticationService.signUp(request))
                .isInstanceOf(AuthenticationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "EMAIL_EXISTS")
                .hasMessageContaining("Email already exists");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("GIVEN weak password without uppercase WHEN signUp THEN throws ValidationException")
        void signUp_givenPasswordWithoutUppercase_shouldThrowException() {
            // GIVEN
            SignUpRequest request = TestDataFactory.createSignUpRequestWithPassword(
                TestDataFactory.WEAK_PASSWORD_NO_UPPERCASE);
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);

            // WHEN / THEN
            assertThatThrownBy(() -> authenticationService.signUp(request))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "VALIDATION_ERROR");
        }

        @Test
        @DisplayName("GIVEN weak password without lowercase WHEN signUp THEN throws ValidationException")
        void signUp_givenPasswordWithoutLowercase_shouldThrowException() {
            // GIVEN
            SignUpRequest request = TestDataFactory.createSignUpRequestWithPassword(
                TestDataFactory.WEAK_PASSWORD_NO_LOWERCASE);
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);

            // WHEN / THEN
            assertThatThrownBy(() -> authenticationService.signUp(request))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "VALIDATION_ERROR");
        }

        @Test
        @DisplayName("GIVEN weak password without digit WHEN signUp THEN throws ValidationException")
        void signUp_givenPasswordWithoutDigit_shouldThrowException() {
            // GIVEN
            SignUpRequest request = TestDataFactory.createSignUpRequestWithPassword(
                TestDataFactory.WEAK_PASSWORD_NO_DIGIT);
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);

            // WHEN / THEN
            assertThatThrownBy(() -> authenticationService.signUp(request))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "VALIDATION_ERROR");
        }

        @Test
        @DisplayName("GIVEN weak password without special char WHEN signUp THEN throws ValidationException")
        void signUp_givenPasswordWithoutSpecialChar_shouldThrowException() {
            // GIVEN
            SignUpRequest request = TestDataFactory.createSignUpRequestWithPassword(
                TestDataFactory.WEAK_PASSWORD_NO_SPECIAL);
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);

            // WHEN / THEN
            assertThatThrownBy(() -> authenticationService.signUp(request))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "VALIDATION_ERROR");
        }

        @Test
        @DisplayName("GIVEN password too short WHEN signUp THEN throws ValidationException")
        void signUp_givenPasswordTooShort_shouldThrowException() {
            // GIVEN
            SignUpRequest request = TestDataFactory.createSignUpRequestWithPassword(
                TestDataFactory.WEAK_PASSWORD_TOO_SHORT);
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);

            // WHEN / THEN
            assertThatThrownBy(() -> authenticationService.signUp(request))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "VALIDATION_ERROR");
        }

        @Test
        @DisplayName("GIVEN null request WHEN signUp THEN throws ValidationException")
        void signUp_givenNullRequest_shouldThrowException() {
            // WHEN / THEN
            assertThatThrownBy(() -> authenticationService.signUp(null))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "VALIDATION_ERROR");
        }

        @Test
        @DisplayName("GIVEN empty username WHEN signUp THEN throws ValidationException")
        void signUp_givenEmptyUsername_shouldThrowException() {
            // GIVEN
            SignUpRequest request = TestDataFactory.createValidSignUpRequest();
            request.setUsername("");

            // WHEN / THEN
            assertThatThrownBy(() -> authenticationService.signUp(request))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "VALIDATION_ERROR")
                .hasMessageContaining("Username is required");
        }

        @Test
        @DisplayName("GIVEN null email WHEN signUp THEN throws ValidationException")
        void signUp_givenNullEmail_shouldThrowException() {
            // GIVEN
            SignUpRequest request = TestDataFactory.createValidSignUpRequest();
            request.setEmail(null);

            // WHEN / THEN
            assertThatThrownBy(() -> authenticationService.signUp(request))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "VALIDATION_ERROR")
                .hasMessageContaining("Email is required");
        }

        @Test
        @DisplayName("GIVEN signup WHEN password encoded THEN uses PasswordEncoder")
        void signUp_givenValidPassword_shouldEncodePassword() {
            // GIVEN
            SignUpRequest request = TestDataFactory.createValidSignUpRequest();
            String expectedHash = "$2a$10$hashedPassword";

            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(request.getPassword())).thenReturn(expectedHash);
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            // WHEN
            authenticationService.signUp(request);

            // THEN
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertThat(userCaptor.getValue().getPasswordHash()).isEqualTo(expectedHash);
            verify(passwordEncoder).encode(request.getPassword());
        }
    }

    // ============ SIGNIN TESTS ============

    @Nested
    @DisplayName("Sign In Tests")
    class SignInTests {

        @Test
        @DisplayName("GIVEN valid credentials WHEN signIn THEN returns user")
        void signIn_givenValidCredentials_shouldReturnUser() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            SignInRequest request = TestDataFactory.createSignInRequestWithCredentials(
                user.getUsername(), TestDataFactory.VALID_PASSWORD);

            when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.getPassword(), user.getPasswordHash())).thenReturn(true);

            // WHEN
            User result = authenticationService.signIn(request);

            // THEN
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo(user.getUsername());
        }

        @Test
        @DisplayName("GIVEN valid email login WHEN signIn THEN returns user")
        void signIn_givenValidEmailCredentials_shouldReturnUser() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            SignInRequest request = TestDataFactory.createSignInRequestWithCredentials(
                user.getEmail(), TestDataFactory.VALID_PASSWORD);

            when(userRepository.findByUsername(user.getEmail())).thenReturn(Optional.empty());
            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.getPassword(), user.getPasswordHash())).thenReturn(true);

            // WHEN
            User result = authenticationService.signIn(request);

            // THEN
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo(user.getEmail());
        }

        @Test
        @DisplayName("GIVEN wrong password WHEN signIn THEN throws AuthenticationException")
        void signIn_givenWrongPassword_shouldThrowException() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            SignInRequest request = TestDataFactory.createSignInRequestWithCredentials(
                user.getUsername(), "wrongpassword");

            when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

            // WHEN / THEN
            assertThatThrownBy(() -> authenticationService.signIn(request))
                .isInstanceOf(AuthenticationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "INVALID_CREDENTIALS")
                .hasMessageContaining("invalid_credentials");
        }

        @Test
        @DisplayName("GIVEN non-existent user WHEN signIn THEN throws AuthenticationException")
        void signIn_givenNonExistentUser_shouldThrowException() {
            // GIVEN
            SignInRequest request = TestDataFactory.createSignInRequestWithCredentials(
                "nonexistent", "password");

            when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
            when(userRepository.findByEmail("nonexistent")).thenReturn(Optional.empty());

            // WHEN / THEN
            assertThatThrownBy(() -> authenticationService.signIn(request))
                .isInstanceOf(AuthenticationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "INVALID_CREDENTIALS")
                .hasMessageContaining("invalid_credentials");
        }

        @Test
        @DisplayName("GIVEN inactive user WHEN signIn THEN throws AuthenticationException")
        void signIn_givenInactiveUser_shouldThrowException() {
            // GIVEN
            User user = TestDataFactory.createInactiveUser();
            SignInRequest request = TestDataFactory.createSignInRequestWithCredentials(
                user.getUsername(), TestDataFactory.VALID_PASSWORD);

            when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

            // WHEN / THEN
            assertThatThrownBy(() -> authenticationService.signIn(request))
                .isInstanceOf(AuthenticationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "ACCOUNT_INACTIVE")
                .hasMessageContaining("User account is not active");
        }

        @Test
        @DisplayName("GIVEN blocked user WHEN signIn THEN throws AuthenticationException")
        void signIn_givenBlockedUser_shouldThrowException() {
            // GIVEN
            User user = TestDataFactory.createBlockedUser();
            SignInRequest request = TestDataFactory.createSignInRequestWithCredentials(
                user.getUsername(), TestDataFactory.VALID_PASSWORD);

            when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

            // WHEN / THEN
            assertThatThrownBy(() -> authenticationService.signIn(request))
                .isInstanceOf(AuthenticationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "ACCOUNT_BLOCKED")
                .hasMessageContaining("User account is blocked");
        }

        @Test
        @DisplayName("GIVEN deleted user WHEN signIn THEN throws AuthenticationException")
        void signIn_givenDeletedUser_shouldThrowException() {
            // GIVEN
            User user = TestDataFactory.createDeletedUser();
            SignInRequest request = TestDataFactory.createSignInRequestWithCredentials(
                user.getUsername(), TestDataFactory.VALID_PASSWORD);

            when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

            // WHEN / THEN
            assertThatThrownBy(() -> authenticationService.signIn(request))
                .isInstanceOf(AuthenticationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "INVALID_CREDENTIALS")
                .hasMessageContaining("invalid_credentials");
        }

        @Test
        @DisplayName("GIVEN null request WHEN signIn THEN throws ValidationException")
        void signIn_givenNullRequest_shouldThrowException() {
            // WHEN / THEN
            assertThatThrownBy(() -> authenticationService.signIn(null))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "VALIDATION_ERROR");
        }

        @Test
        @DisplayName("GIVEN empty username WHEN signIn THEN throws ValidationException")
        void signIn_givenEmptyUsername_shouldThrowException() {
            // GIVEN
            SignInRequest request = new SignInRequest();
            request.setUsername("");
            request.setPassword("password");

            // WHEN / THEN
            assertThatThrownBy(() -> authenticationService.signIn(request))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "VALIDATION_ERROR")
                .hasMessageContaining("Username or email is required");
        }

        @Test
        @DisplayName("GIVEN null password WHEN signIn THEN throws ValidationException")
        void signIn_givenNullPassword_shouldThrowException() {
            // GIVEN
            SignInRequest request = new SignInRequest();
            request.setUsername("testuser");
            request.setPassword(null);

            // WHEN / THEN
            assertThatThrownBy(() -> authenticationService.signIn(request))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "VALIDATION_ERROR")
                .hasMessageContaining("Password is required");
        }

        @Test
        @DisplayName("GIVEN error message WHEN wrong password THEN does not reveal valid username")
        void signIn_givenWrongPassword_shouldNotRevealValidUsername() {
            // GIVEN - Security test: error message should be generic
            User user = TestDataFactory.createActiveUser();
            SignInRequest request = TestDataFactory.createSignInRequestWithCredentials(
                user.getUsername(), "wrongpassword");

            when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

            // WHEN / THEN - Same error message as non-existent user
            assertThatThrownBy(() -> authenticationService.signIn(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("invalid_credentials");
        }
    }

    // ============ USER EXISTENCE TESTS ============

    @Nested
    @DisplayName("User Existence Tests")
    class UserExistenceTests {

        @Test
        @DisplayName("GIVEN existing username WHEN userExists THEN returns true")
        void userExists_givenExistingUsername_shouldReturnTrue() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            when(userRepository.findByUsernameAndIsDeletedFalse(user.getUsername()))
                .thenReturn(Optional.of(user));

            // WHEN
            boolean result = authenticationService.userExists(user.getUsername());

            // THEN
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("GIVEN non-existing username WHEN userExists THEN returns false")
        void userExists_givenNonExistingUsername_shouldReturnFalse() {
            // GIVEN
            when(userRepository.findByUsernameAndIsDeletedFalse("nonexistent"))
                .thenReturn(Optional.empty());

            // WHEN
            boolean result = authenticationService.userExists("nonexistent");

            // THEN
            assertThat(result).isFalse();
        }
    }
}

