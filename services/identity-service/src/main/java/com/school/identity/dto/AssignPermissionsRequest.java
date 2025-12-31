package com.school.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

/**
 * Request for assigning permissions to a role
 */
public class AssignPermissionsRequest {

    @NotBlank(message = "Role ID is required")
    private String roleId;

    @NotEmpty(message = "At least one permission ID is required")
    private Set<String> permissionIds;

    // Constructors
    public AssignPermissionsRequest() {
    }

    public AssignPermissionsRequest(String roleId, Set<String> permissionIds) {
        this.roleId = roleId;
        this.permissionIds = permissionIds;
    }

    // Getters and Setters
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public Set<String> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(Set<String> permissionIds) {
        this.permissionIds = permissionIds;
    }
}

