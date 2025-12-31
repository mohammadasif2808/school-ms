package com.school.identity.exception;

import com.school.identity.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for all controllers
 *
 * Standardizes error responses across all endpoints
 * Handles:
 * - Validation errors (400)
 * - Custom validation exceptions (400)
 * - Authentication errors (401)
 * - Authorization/permission errors (403)
 * - Not found errors (404)
 * - Unexpected errors (500)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle custom ValidationException
     *
     * Thrown by services for business rule violations
     * Returns 400 Bad Request
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException e,
            WebRequest request) {

        logger.warn("Validation error: {} - {}", e.getErrorCode(), e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            e.getErrorCode(),
            e.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        errorResponse.setMethod(request.getDescription(false));

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse);
    }

    /**
     * Handle @Valid annotation validation errors
     *
     * Thrown by Spring when DTO fields fail validation
     * Extracts field-level errors and returns them
     * Returns 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            WebRequest request) {

        logger.warn("Request validation failed");

        // Extract field errors
        Map<String, String> fieldErrors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "VALIDATION_ERROR",
            "Request validation failed",
            fieldErrors
        );
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse);
    }

    /**
     * Handle Spring Security authentication errors
     *
     * Thrown when JWT validation fails or token is missing
     * Returns 401 Unauthorized
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException e,
            WebRequest request) {

        logger.warn("Authentication error: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "UNAUTHORIZED",
            e.getMessage() != null ? e.getMessage() : "Authentication failed"
        );
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(errorResponse);
    }

    /**
     * Handle Spring Security authorization errors
     *
     * Thrown when user lacks required permission
     * Returns 403 Forbidden
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException e,
            WebRequest request) {

        logger.warn("Access denied: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "FORBIDDEN",
            "Access denied - insufficient permissions"
        );
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(errorResponse);
    }

    /**
     * Handle 404 Not Found errors
     *
     * Thrown when endpoint doesn't exist
     * Returns 404 Not Found
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
            NoHandlerFoundException e,
            WebRequest request) {

        logger.warn("Endpoint not found: {} {}", e.getHttpMethod(), e.getRequestURL());

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "NOT_FOUND",
            "Requested endpoint not found"
        );
        errorResponse.setPath(e.getRequestURL());
        errorResponse.setMethod(e.getHttpMethod());

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(errorResponse);
    }

    /**
     * Handle all other unexpected exceptions
     *
     * Fallback for any unhandled exception
     * Logs full stack trace for debugging
     * Returns 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception e,
            WebRequest request) {

        logger.error("Unexpected error", e);

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred"
        );
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse);
    }
}

