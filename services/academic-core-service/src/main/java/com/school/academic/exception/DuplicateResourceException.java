package com.school.academic.exception;

/**
 * Exception thrown when a duplicate resource is detected.
 */
public class DuplicateResourceException extends RuntimeException {

    private final String code;

    public DuplicateResourceException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
