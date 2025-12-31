package com.school.identity.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for Permission
 */
public class PermissionResponse {

    private UUID id;
    private String code;
    private String module;
    private String description;
    private LocalDateTime createdAt;

    // Constructors
    public PermissionResponse() {
    }

    public PermissionResponse(UUID id, String code, String module, String description, LocalDateTime createdAt) {
        this.id = id;
        this.code = code;
        this.module = module;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

