package com.school.academic.exception;

/**
 * Exception thrown when an invalid state transition is attempted.
 * Used for domain objects with state machines (e.g., complaint status, enquiry status).
 */
public class InvalidStateTransitionException extends RuntimeException {

    private final String code;
    private final String currentState;
    private final String targetState;

    public InvalidStateTransitionException(String code, String message) {
        super(message);
        this.code = code;
        this.currentState = null;
        this.targetState = null;
    }

    public InvalidStateTransitionException(String code, String currentState, String targetState, String message) {
        super(message);
        this.code = code;
        this.currentState = currentState;
        this.targetState = targetState;
    }

    public String getCode() {
        return code;
    }

    public String getCurrentState() {
        return currentState;
    }

    public String getTargetState() {
        return targetState;
    }
}
