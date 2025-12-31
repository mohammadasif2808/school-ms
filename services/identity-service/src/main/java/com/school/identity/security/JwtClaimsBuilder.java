package com.school.identity.security;

import com.school.identity.domain.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper component for building JWT claims from User entity
 * Extracts permissions from roles and handles role/permission mapping
 */
@Component
public class JwtClaimsBuilder {

    /**
     * Extract permission codes from user's roles
     *
     * @param user the user entity with roles populated
     * @return list of permission codes from all assigned roles
     */
    public List<String> extractPermissionsFromRoles(User user) {
        if (user == null || user.getRoles() == null || user.getRoles().isEmpty()) {
            return List.of();
        }

        return user.getRoles().stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(permission -> permission.getCode())
            .distinct()
            .collect(Collectors.toList());
    }

    /**
     * Extract primary role name from user
     * Returns the first active role name
     *
     * @param user the user entity with roles populated
     * @return role name or "USER" if no roles assigned
     */
    public String extractPrimaryRoleName(User user) {
        if (user == null || user.getRoles() == null || user.getRoles().isEmpty()) {
            return "USER";
        }

        return user.getRoles().stream()
            .filter(role -> role.getStatus() != null)
            .map(role -> role.getName().toUpperCase())
            .findFirst()
            .orElse("USER");
    }

    /**
     * Get all role names assigned to user (comma-separated)
     * Useful for logging and audit purposes
     *
     * @param user the user entity with roles populated
     * @return comma-separated role names
     */
    public String getAllRoleNames(User user) {
        if (user == null || user.getRoles() == null || user.getRoles().isEmpty()) {
            return "";
        }

        return user.getRoles().stream()
            .map(role -> role.getName())
            .collect(Collectors.joining(", "));
    }

    /**
     * Check if user has a specific permission
     *
     * @param user the user entity with roles populated
     * @param permissionCode the permission code to check
     * @return true if user has this permission
     */
    public boolean hasPermission(User user, String permissionCode) {
        if (user == null || user.getRoles() == null || user.getRoles().isEmpty()) {
            return false;
        }

        return user.getRoles().stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(permission -> permission.getCode())
            .anyMatch(code -> code.equals(permissionCode));
    }

    /**
     * Check if user has super admin privilege
     * Super admins bypass permission checks
     *
     * @param user the user entity
     * @return true if user is super admin
     */
    public boolean isSuperAdmin(User user) {
        return user != null && Boolean.TRUE.equals(user.getIsSuperAdmin());
    }
}

