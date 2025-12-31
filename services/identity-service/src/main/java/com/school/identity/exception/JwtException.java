package com.school.identity.exception;

/**
 * Exception for JWT-related errors
 */
public class JwtException extends RuntimeException {

    private final String errorCode;

    public JwtException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public JwtException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public JwtException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

