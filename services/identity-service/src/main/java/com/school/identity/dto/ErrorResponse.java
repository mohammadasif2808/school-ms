package com.school.identity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response DTO for all API endpoints
 *
 * All errors return this consistent format across identity-service
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * HTTP status code (e.g., 400, 401, 403, 404, 500)
     */
    private Integer status;

    /**
     * Machine-readable error code for client-side handling
     * Examples: VALIDATION_ERROR, UNAUTHORIZED, FORBIDDEN, NOT_FOUND, INTERNAL_SERVER_ERROR
     */
    private String error;

    /**
     * Human-readable error message
     */
    private String message;

    /**
     * Detailed validation errors (field → error message)
     * Only populated for validation errors
     */
    private Map<String, String> details;

    /**
     * ISO-8601 timestamp when error occurred
     */
    private LocalDateTime timestamp;

    /**
     * HTTP method (GET, POST, etc.)
     */
    private String method;

    /**
     * Request path
     */
    private String path;

    // Constructors
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(Integer status, String error, String message) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public ErrorResponse(Integer status, String error, String message, String method, String path) {
        this(status, error, message);
        this.method = method;
        this.path = path;
    }

    public ErrorResponse(Integer status, String error, String message, Map<String, String> details) {
        this(status, error, message);
        this.details = details;
    }

    // Getters and Setters
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

