package com.school.identity.exception;

import com.school.identity.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GlobalExceptionHandler
 *
 * Tests exception handling and error response format
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
        // Use lenient stubbing - some tests don't use webRequest but it's set up globally
        lenient().when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/test");
    }

    // ============ VALIDATION EXCEPTION TESTS ============

    @Nested
    @DisplayName("ValidationException Tests")
    class ValidationExceptionTests {

        @Test
        @DisplayName("GIVEN ValidationException WHEN handled THEN returns 400 with correct format")
        void handleValidationException_shouldReturn400() {
            // GIVEN
            ValidationException exception = new ValidationException("USERNAME_EXISTS", "Username already exists");

            // WHEN
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception, webRequest);

            // THEN
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(400);
            assertThat(response.getBody().getError()).isEqualTo("USERNAME_EXISTS");
            assertThat(response.getBody().getMessage()).isEqualTo("Username already exists");
        }

        @Test
        @DisplayName("GIVEN ValidationException WHEN handled THEN response has timestamp")
        void handleValidationException_shouldHaveTimestamp() {
            // GIVEN
            ValidationException exception = new ValidationException("ERROR", "Message");

            // WHEN
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception, webRequest);

            // THEN
            assertThat(response.getBody().getTimestamp()).isNotNull();
        }
    }

    // ============ METHOD ARGUMENT NOT VALID TESTS ============

    @Nested
    @DisplayName("MethodArgumentNotValidException Tests")
    class MethodArgumentNotValidTests {

        @Test
        @DisplayName("GIVEN validation errors WHEN handled THEN returns 400 with field details")
        void handleMethodArgumentNotValid_shouldReturn400WithDetails() {
            // GIVEN
            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);

            FieldError usernameError = new FieldError("signUpRequest", "username", "Username is required");
            FieldError emailError = new FieldError("signUpRequest", "email", "Email must be valid");

            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getAllErrors()).thenReturn(List.of(usernameError, emailError));

            // WHEN
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodArgumentNotValid(exception, webRequest);

            // THEN
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(400);
            assertThat(response.getBody().getError()).isEqualTo("VALIDATION_ERROR");
            assertThat(response.getBody().getMessage()).isEqualTo("Request validation failed");
            assertThat(response.getBody().getDetails()).containsEntry("username", "Username is required");
            assertThat(response.getBody().getDetails()).containsEntry("email", "Email must be valid");
        }

        @Test
        @DisplayName("GIVEN single validation error WHEN handled THEN includes field in details")
        void handleMethodArgumentNotValid_singleError_shouldIncludeField() {
            // GIVEN
            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);

            FieldError passwordError = new FieldError("request", "password", "Password is too weak");

            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getAllErrors()).thenReturn(List.of(passwordError));

            // WHEN
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodArgumentNotValid(exception, webRequest);

            // THEN
            assertThat(response.getBody().getDetails()).hasSize(1);
            assertThat(response.getBody().getDetails()).containsEntry("password", "Password is too weak");
        }
    }

    // ============ AUTHENTICATION EXCEPTION TESTS ============

    @Nested
    @DisplayName("AuthenticationException Tests")
    class AuthenticationExceptionTests {

        @Test
        @DisplayName("GIVEN AuthenticationException WHEN handled THEN returns 401")
        void handleAuthenticationException_shouldReturn401() {
            // GIVEN
            org.springframework.security.core.AuthenticationException exception =
                new BadCredentialsException("Invalid credentials");

            // WHEN
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleAuthenticationException(exception, webRequest);

            // THEN
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(401);
            assertThat(response.getBody().getError()).isEqualTo("UNAUTHORIZED");
        }

        @Test
        @DisplayName("GIVEN AuthenticationException with null message WHEN handled THEN uses default message")
        void handleAuthenticationException_nullMessage_shouldUseDefault() {
            // GIVEN
            org.springframework.security.core.AuthenticationException exception =
                new BadCredentialsException(null);

            // WHEN
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleAuthenticationException(exception, webRequest);

            // THEN
            assertThat(response.getBody().getMessage()).isEqualTo("Authentication failed");
        }
    }

    // ============ ACCESS DENIED EXCEPTION TESTS ============

    @Nested
    @DisplayName("AccessDeniedException Tests")
    class AccessDeniedExceptionTests {

        @Test
        @DisplayName("GIVEN AccessDeniedException WHEN handled THEN returns 403")
        void handleAccessDeniedException_shouldReturn403() {
            // GIVEN
            AccessDeniedException exception = new AccessDeniedException("Access denied");

            // WHEN
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDeniedException(exception, webRequest);

            // THEN
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(403);
            assertThat(response.getBody().getError()).isEqualTo("FORBIDDEN");
            assertThat(response.getBody().getMessage()).isEqualTo("Access denied - insufficient permissions");
        }
    }

    // ============ NO HANDLER FOUND EXCEPTION TESTS ============

    @Nested
    @DisplayName("NoHandlerFoundException Tests")
    class NoHandlerFoundExceptionTests {

        @Test
        @DisplayName("GIVEN NoHandlerFoundException WHEN handled THEN returns 404")
        void handleNoHandlerFoundException_shouldReturn404() {
            // GIVEN
            NoHandlerFoundException exception = new NoHandlerFoundException(
                "GET", "/api/v1/nonexistent", org.springframework.http.HttpHeaders.EMPTY);

            // WHEN
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleNoHandlerFoundException(exception, webRequest);

            // THEN
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(404);
            assertThat(response.getBody().getError()).isEqualTo("NOT_FOUND");
            assertThat(response.getBody().getMessage()).isEqualTo("Requested endpoint not found");
        }

        @Test
        @DisplayName("GIVEN NoHandlerFoundException WHEN handled THEN includes method and path")
        void handleNoHandlerFoundException_shouldIncludeMethodAndPath() {
            // GIVEN
            NoHandlerFoundException exception = new NoHandlerFoundException(
                "POST", "/api/v1/invalid", org.springframework.http.HttpHeaders.EMPTY);

            // WHEN
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleNoHandlerFoundException(exception, webRequest);

            // THEN
            assertThat(response.getBody().getMethod()).isEqualTo("POST");
            assertThat(response.getBody().getPath()).isEqualTo("/api/v1/invalid");
        }
    }

    // ============ GENERIC EXCEPTION TESTS ============

    @Nested
    @DisplayName("Generic Exception Tests")
    class GenericExceptionTests {

        @Test
        @DisplayName("GIVEN unexpected exception WHEN handled THEN returns 500")
        void handleGlobalException_shouldReturn500() {
            // GIVEN
            Exception exception = new RuntimeException("Unexpected error");

            // WHEN
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(exception, webRequest);

            // THEN
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(500);
            assertThat(response.getBody().getError()).isEqualTo("INTERNAL_SERVER_ERROR");
        }

        @Test
        @DisplayName("GIVEN unexpected exception WHEN handled THEN does not expose stack trace")
        void handleGlobalException_shouldNotExposeStackTrace() {
            // GIVEN
            Exception exception = new NullPointerException("Sensitive error details");

            // WHEN
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(exception, webRequest);

            // THEN
            assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
            assertThat(response.getBody().getMessage()).doesNotContain("NullPointerException");
            assertThat(response.getBody().getMessage()).doesNotContain("Sensitive");
        }

        @Test
        @DisplayName("GIVEN unexpected exception WHEN handled THEN uses generic message")
        void handleGlobalException_shouldUseGenericMessage() {
            // GIVEN - Security: never expose internal details
            Exception exception = new RuntimeException("Database connection failed to mysql://internal:3306");

            // WHEN
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(exception, webRequest);

            // THEN
            assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
            assertThat(response.getBody().getMessage()).doesNotContain("mysql");
            assertThat(response.getBody().getMessage()).doesNotContain("Database");
        }
    }

    // ============ ERROR RESPONSE FORMAT TESTS ============

    @Nested
    @DisplayName("Error Response Format Tests")
    class ErrorResponseFormatTests {

        @Test
        @DisplayName("GIVEN any exception WHEN handled THEN response has required fields")
        void errorResponse_shouldHaveRequiredFields() {
            // GIVEN
            ValidationException exception = new ValidationException("ERROR", "Message");

            // WHEN
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception, webRequest);

            // THEN
            assertThat(response.getBody().getStatus()).isNotNull();
            assertThat(response.getBody().getError()).isNotNull();
            assertThat(response.getBody().getMessage()).isNotNull();
            assertThat(response.getBody().getTimestamp()).isNotNull();
        }

        @Test
        @DisplayName("GIVEN exception WHEN handled THEN timestamp is recent")
        void errorResponse_timestampShouldBeRecent() {
            // GIVEN
            ValidationException exception = new ValidationException("ERROR", "Message");
            java.time.LocalDateTime before = java.time.LocalDateTime.now().minusSeconds(1);

            // WHEN
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception, webRequest);

            // THEN
            assertThat(response.getBody().getTimestamp()).isAfter(before);
            assertThat(response.getBody().getTimestamp()).isBefore(java.time.LocalDateTime.now().plusSeconds(1));
        }
    }

    // ============ SECURITY EDGE CASES ============

    @Nested
    @DisplayName("Security Edge Cases")
    class SecurityEdgeCases {

        @Test
        @DisplayName("GIVEN SQL exception WHEN handled THEN does not expose SQL details")
        void handleException_sqlError_shouldNotExposeSql() {
            // GIVEN
            Exception exception = new RuntimeException("SELECT * FROM users WHERE...");

            // WHEN
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(exception, webRequest);

            // THEN
            assertThat(response.getBody().getMessage()).doesNotContain("SELECT");
            assertThat(response.getBody().getMessage()).doesNotContain("FROM");
            assertThat(response.getBody().getMessage()).doesNotContain("users");
        }

        @Test
        @DisplayName("GIVEN exception with sensitive data WHEN handled THEN data is not exposed")
        void handleException_sensitiveData_shouldNotExpose() {
            // GIVEN
            Exception exception = new RuntimeException("User password123 failed authentication");

            // WHEN
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(exception, webRequest);

            // THEN
            assertThat(response.getBody().getMessage()).doesNotContain("password");
            assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
        }

        @Test
        @DisplayName("GIVEN exception with file path WHEN handled THEN path is not exposed")
        void handleException_filePath_shouldNotExpose() {
            // GIVEN
            Exception exception = new RuntimeException("File not found: /var/secrets/key.pem");

            // WHEN
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(exception, webRequest);

            // THEN
            assertThat(response.getBody().getMessage()).doesNotContain("/var");
            assertThat(response.getBody().getMessage()).doesNotContain("secrets");
            assertThat(response.getBody().getMessage()).doesNotContain(".pem");
        }
    }

    // ============ HTTP STATUS CODES ============

    @Nested
    @DisplayName("HTTP Status Codes Tests")
    class HttpStatusCodesTests {

        @Test
        @DisplayName("GIVEN ValidationException WHEN handled THEN HTTP status matches body status")
        void validationException_statusShouldMatch() {
            ValidationException exception = new ValidationException("ERROR", "Message");
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception, webRequest);

            assertThat(response.getStatusCodeValue()).isEqualTo(response.getBody().getStatus());
        }

        @Test
        @DisplayName("GIVEN AuthenticationException WHEN handled THEN HTTP status matches body status")
        void authenticationException_statusShouldMatch() {
            org.springframework.security.core.AuthenticationException exception =
                new BadCredentialsException("Invalid");
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleAuthenticationException(exception, webRequest);

            assertThat(response.getStatusCodeValue()).isEqualTo(response.getBody().getStatus());
        }

        @Test
        @DisplayName("GIVEN AccessDeniedException WHEN handled THEN HTTP status matches body status")
        void accessDeniedException_statusShouldMatch() {
            AccessDeniedException exception = new AccessDeniedException("Denied");
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDeniedException(exception, webRequest);

            assertThat(response.getStatusCodeValue()).isEqualTo(response.getBody().getStatus());
        }

        @Test
        @DisplayName("GIVEN Exception WHEN handled THEN HTTP status matches body status")
        void exception_statusShouldMatch() {
            Exception exception = new RuntimeException("Error");
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(exception, webRequest);

            assertThat(response.getStatusCodeValue()).isEqualTo(response.getBody().getStatus());
        }
    }
}

