package com.school.identity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.identity.domain.User;
import com.school.identity.dto.SignInRequest;
import com.school.identity.dto.SignUpRequest;
import com.school.identity.exception.AuthenticationException;
import com.school.identity.exception.GlobalExceptionHandler;
import com.school.identity.exception.ValidationException;
import com.school.identity.security.JwtAuthenticationFilter;
import com.school.identity.service.AuthenticationService;
import com.school.identity.service.JwtService;
import com.school.identity.service.PasswordResetService;
import com.school.identity.testutil.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AuthenticationController
 *
 * Tests REST endpoint behavior using MockMvc (without full Spring context)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationController Tests")
class AuthenticationControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordResetService passwordResetService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders
            .standaloneSetup(authenticationController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    // ============ SIGNUP ENDPOINT TESTS ============

    @Nested
    @DisplayName("POST /api/v1/auth/signup Tests")
    class SignUpEndpointTests {

        @Test
        @DisplayName("GIVEN valid signup request WHEN POST /signup THEN returns 201 with user")
        void signup_givenValidRequest_shouldReturn201() throws Exception {
            // GIVEN
            SignUpRequest request = TestDataFactory.createValidSignUpRequest();
            User createdUser = TestDataFactory.createActiveUser();
            createdUser.setUsername(request.getUsername());
            createdUser.setEmail(request.getEmail());

            when(authenticationService.signUp(any(SignUpRequest.class))).thenReturn(createdUser);

            // WHEN / THEN
            mockMvc.perform(post("/api/v1/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(request.getUsername()))
                .andExpect(jsonPath("$.email").value(request.getEmail()))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
        }

        @Test
        @DisplayName("GIVEN duplicate username WHEN POST /signup THEN returns 409")
        void signup_givenDuplicateUsername_shouldReturn409() throws Exception {
            // GIVEN
            SignUpRequest request = TestDataFactory.createValidSignUpRequest();

            when(authenticationService.signUp(any(SignUpRequest.class)))
                .thenThrow(new AuthenticationException("USERNAME_EXISTS", "Username already exists"));

            // WHEN / THEN
            mockMvc.perform(post("/api/v1/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("USERNAME_EXISTS"));
        }

        @Test
        @DisplayName("GIVEN duplicate email WHEN POST /signup THEN returns 409")
        void signup_givenDuplicateEmail_shouldReturn409() throws Exception {
            // GIVEN
            SignUpRequest request = TestDataFactory.createValidSignUpRequest();

            when(authenticationService.signUp(any(SignUpRequest.class)))
                .thenThrow(new AuthenticationException("EMAIL_EXISTS", "Email already exists"));

            // WHEN / THEN
            mockMvc.perform(post("/api/v1/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("EMAIL_EXISTS"));
        }

        @Test
        @DisplayName("GIVEN weak password WHEN POST /signup THEN returns 400")
        void signup_givenWeakPassword_shouldReturn400() throws Exception {
            // GIVEN - Use password that passes DTO validation (8+ chars) but service rejects
            SignUpRequest request = TestDataFactory.createSignUpRequestWithPassword("weakpass");

            when(authenticationService.signUp(any(SignUpRequest.class)))
                .thenThrow(new ValidationException("PASSWORD_WEAK", "Password must contain..."));

            // WHEN / THEN
            mockMvc.perform(post("/api/v1/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("PASSWORD_WEAK"));
        }

        @Test
        @DisplayName("GIVEN empty request body WHEN POST /signup THEN returns 400")
        void signup_givenEmptyBody_shouldReturn400() throws Exception {
            // WHEN / THEN
            mockMvc.perform(post("/api/v1/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                .andExpect(status().isBadRequest());
        }
    }

    // ============ SIGNIN ENDPOINT TESTS ============

    @Nested
    @DisplayName("POST /api/v1/auth/signin Tests")
    class SignInEndpointTests {

        @Test
        @DisplayName("GIVEN valid credentials WHEN POST /signin THEN returns 200 with token")
        void signin_givenValidCredentials_shouldReturn200WithToken() throws Exception {
            // GIVEN
            SignInRequest request = TestDataFactory.createValidSignInRequest();
            User user = TestDataFactory.createActiveUser();
            String token = "generated.jwt.token";

            when(authenticationService.signIn(any(SignInRequest.class))).thenReturn(user);
            when(jwtService.generateToken(user)).thenReturn(token);

            // WHEN / THEN
            mockMvc.perform(post("/api/v1/auth/signin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(token))
                .andExpect(jsonPath("$.user.username").value(user.getUsername()));
        }

        @Test
        @DisplayName("GIVEN invalid credentials WHEN POST /signin THEN returns 401")
        void signin_givenInvalidCredentials_shouldReturn401() throws Exception {
            // GIVEN
            SignInRequest request = TestDataFactory.createSignInRequestWithCredentials("wrong", "wrong");

            when(authenticationService.signIn(any(SignInRequest.class)))
                .thenThrow(new AuthenticationException("INVALID_CREDENTIALS", "Invalid username or password"));

            // WHEN / THEN
            mockMvc.perform(post("/api/v1/auth/signin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("INVALID_CREDENTIALS"));
        }

        @Test
        @DisplayName("GIVEN inactive account WHEN POST /signin THEN returns 403 with ACCOUNT_INACTIVE")
        void signin_givenInactiveAccount_shouldReturn403() throws Exception {
            // GIVEN
            SignInRequest request = TestDataFactory.createValidSignInRequest();

            when(authenticationService.signIn(any(SignInRequest.class)))
                .thenThrow(new AuthenticationException("ACCOUNT_INACTIVE", "User account is not active"));

            // WHEN / THEN
            mockMvc.perform(post("/api/v1/auth/signin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("ACCOUNT_INACTIVE"));
        }

        @Test
        @DisplayName("GIVEN blocked account WHEN POST /signin THEN returns 403 with ACCOUNT_BLOCKED")
        void signin_givenBlockedAccount_shouldReturn403() throws Exception {
            // GIVEN
            SignInRequest request = TestDataFactory.createValidSignInRequest();

            when(authenticationService.signIn(any(SignInRequest.class)))
                .thenThrow(new AuthenticationException("ACCOUNT_BLOCKED", "User account is blocked"));

            // WHEN / THEN
            mockMvc.perform(post("/api/v1/auth/signin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("ACCOUNT_BLOCKED"));
        }

        @Test
        @DisplayName("GIVEN missing username WHEN POST /signin THEN returns 400")
        void signin_givenMissingUsername_shouldReturn400() throws Exception {
            // GIVEN - DTO validation catches missing username before service call
            SignInRequest request = new SignInRequest();
            request.setPassword("password");
            // No mock needed - @Valid annotation triggers MethodArgumentNotValidException

            // WHEN / THEN
            mockMvc.perform(post("/api/v1/auth/signin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }
    }

    // ============ SIGNOUT ENDPOINT TESTS ============

    @Nested
    @DisplayName("POST /api/v1/auth/signout Tests")
    class SignOutEndpointTests {

        @Test
        @DisplayName("GIVEN valid token WHEN POST /signout THEN returns 200")
        void signout_givenValidToken_shouldReturn200() throws Exception {
            // WHEN / THEN
            mockMvc.perform(post("/api/v1/auth/signout")
                    .header("Authorization", "Bearer valid.jwt.token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successfully signed out"));
        }
    }

    // ============ FORGOT PASSWORD ENDPOINT TESTS ============

    @Nested
    @DisplayName("POST /api/v1/auth/forgot-password Tests")
    class ForgotPasswordEndpointTests {

        @Test
        @DisplayName("GIVEN valid email WHEN POST /forgot-password THEN returns 200")
        void forgotPassword_givenValidEmail_shouldReturn200() throws Exception {
            // GIVEN
            String requestBody = """
                {
                    "email": "user@example.com"
                }
                """;

            // WHEN / THEN
            mockMvc.perform(post("/api/v1/auth/forgot-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @DisplayName("GIVEN non-existent email WHEN POST /forgot-password THEN still returns 200 (no info leak)")
        void forgotPassword_givenNonExistentEmail_shouldReturn200() throws Exception {
            // GIVEN - Security: don't reveal if email exists
            String requestBody = """
                {
                    "email": "nonexistent@example.com"
                }
                """;

            // WHEN / THEN
            mockMvc.perform(post("/api/v1/auth/forgot-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isOk());
        }
    }

    // ============ RESET PASSWORD ENDPOINT TESTS ============

    @Nested
    @DisplayName("POST /api/v1/auth/reset-password Tests")
    class ResetPasswordEndpointTests {

        @Test
        @DisplayName("GIVEN valid token and password WHEN POST /reset-password THEN returns 200")
        void resetPassword_givenValidRequest_shouldReturn200() throws Exception {
            // GIVEN
            String requestBody = """
                {
                    "token": "valid-reset-token",
                    "newPassword": "NewSecure@Pass123"
                }
                """;

            // Mock password reset service (does nothing, just succeeds)
            doNothing().when(passwordResetService).resetPassword(any());

            // WHEN / THEN
            mockMvc.perform(post("/api/v1/auth/reset-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
        }
    }

    // ============ RESPONSE FORMAT TESTS ============

    @Nested
    @DisplayName("Response Format Tests")
    class ResponseFormatTests {

        @Test
        @DisplayName("GIVEN signup success WHEN response returned THEN does not include password")
        void signup_response_shouldNotIncludePassword() throws Exception {
            // GIVEN
            SignUpRequest request = TestDataFactory.createValidSignUpRequest();
            User createdUser = TestDataFactory.createActiveUser();

            when(authenticationService.signUp(any(SignUpRequest.class))).thenReturn(createdUser);

            // WHEN / THEN
            mockMvc.perform(post("/api/v1/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
        }

        @Test
        @DisplayName("GIVEN signin success WHEN response returned THEN includes user info")
        void signin_response_shouldIncludeUserInfo() throws Exception {
            // GIVEN
            SignInRequest request = TestDataFactory.createValidSignInRequest();
            User user = TestDataFactory.createActiveUser();

            when(authenticationService.signIn(any(SignInRequest.class))).thenReturn(user);
            when(jwtService.generateToken(user)).thenReturn("token");

            // WHEN / THEN
            mockMvc.perform(post("/api/v1/auth/signin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").exists())
                .andExpect(jsonPath("$.user.username").exists())
                .andExpect(jsonPath("$.user.email").exists());
        }

        @Test
        @DisplayName("GIVEN error WHEN response returned THEN follows error response format")
        void error_response_shouldFollowFormat() throws Exception {
            // GIVEN
            SignInRequest request = TestDataFactory.createValidSignInRequest();

            // Use INVALID_CREDENTIALS which returns 401 per OpenAPI contract
            when(authenticationService.signIn(any(SignInRequest.class)))
                .thenThrow(new AuthenticationException("INVALID_CREDENTIALS", "Invalid username or password"));

            // WHEN / THEN
            // Controller returns {error, message} format
            mockMvc.perform(post("/api/v1/auth/signin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
        }
    }
}

