package com.school.identity.exception;

/**
 * Exception for authentication-related errors
 */
public class AuthenticationException extends RuntimeException {

    private final String errorCode;

    public AuthenticationException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public AuthenticationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AuthenticationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

