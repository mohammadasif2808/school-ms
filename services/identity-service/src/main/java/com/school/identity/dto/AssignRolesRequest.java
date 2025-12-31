package com.school.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

/**
 * Request for assigning roles to a user
 */
public class AssignRolesRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotEmpty(message = "At least one role ID is required")
    private Set<String> roleIds;

    // Constructors
    public AssignRolesRequest() {
    }

    public AssignRolesRequest(String userId, Set<String> roleIds) {
        this.userId = userId;
        this.roleIds = roleIds;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Set<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Set<String> roleIds) {
        this.roleIds = roleIds;
    }
}

