package com.school.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request for creating a new permission
 */
public class CreatePermissionRequest {

    @NotBlank(message = "Permission code is required")
    @Size(min = 1, max = 100, message = "Permission code must be between 1 and 100 characters")
    private String code;

    @NotBlank(message = "Module is required")
    @Size(min = 1, max = 100, message = "Module must be between 1 and 100 characters")
    private String module;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    // Constructors
    public CreatePermissionRequest() {
    }

    public CreatePermissionRequest(String code, String module, String description) {
        this.code = code;
        this.module = module;
        this.description = description;
    }

    // Getters and Setters
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
}

