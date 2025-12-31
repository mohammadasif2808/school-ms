package com.school.identity.security;

import com.school.identity.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Custom permission evaluator for Spring Security
 *
 * Evaluates permissions based on JWT claims (no database access during authorization)
 * Used with @PreAuthorize("@permissionEvaluator.hasPermission(...)")
 */
@Component
public class PermissionEvaluator {

    /**
     * Check if authenticated user has a specific permission
     * Permissions come from JWT token claims, no database access
     *
     * @param authentication Spring Security authentication object
     * @param permission permission code to check (e.g., "STUDENT_VIEW", "USER_CREATE")
     * @return true if user has permission, false otherwise
     */
    public boolean hasPermission(Authentication authentication, String permission) {
        // Check: user authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Get user from authentication principal
        User user = (User) authentication.getPrincipal();

        // Check: user is super admin (bypass all permission checks)
        if (user != null && Boolean.TRUE.equals(user.getIsSuperAdmin())) {
            return true;
        }

        // Check: user has permission in JWT claims
        // Note: Permissions are populated in SecurityContext by JwtAuthenticationFilter
        // They come from JWT token, not from database
        return hasPermissionInAuthentication(authentication, permission);
    }

    /**
     * Check if user has ANY of the specified permissions
     *
     * @param authentication Spring Security authentication
     * @param permissions list of permission codes (OR logic - if any match, return true)
     * @return true if user has any of the permissions
     */
    public boolean hasAnyPermission(Authentication authentication, String... permissions) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        User user = (User) authentication.getPrincipal();
        if (user != null && Boolean.TRUE.equals(user.getIsSuperAdmin())) {
            return true;
        }

        for (String permission : permissions) {
            if (hasPermissionInAuthentication(authentication, permission)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if user has ALL of the specified permissions
     *
     * @param authentication Spring Security authentication
     * @param permissions list of permission codes (AND logic - all must match)
     * @return true if user has all of the permissions
     */
    public boolean hasAllPermissions(Authentication authentication, String... permissions) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        User user = (User) authentication.getPrincipal();
        if (user != null && Boolean.TRUE.equals(user.getIsSuperAdmin())) {
            return true;
        }

        for (String permission : permissions) {
            if (!hasPermissionInAuthentication(authentication, permission)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check if user has a specific role
     *
     * @param authentication Spring Security authentication
     * @param role role name to check
     * @return true if user has role
     */
    public boolean hasRole(Authentication authentication, String role) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Get user from authentication
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            return false;
        }

        // Check super admin (can assume any role)
        if (Boolean.TRUE.equals(user.getIsSuperAdmin())) {
            return true;
        }

        // Check if user has role
        // Roles are stored in user.roles
        if (user.getRoles() == null) {
            return false;
        }

        return user.getRoles().stream()
            .anyMatch(r -> role.equalsIgnoreCase(r.getName()));
    }

    /**
     * Check if user has ANY of the specified roles
     *
     * @param authentication Spring Security authentication
     * @param roles role names to check (OR logic)
     * @return true if user has any of the roles
     */
    public boolean hasAnyRole(Authentication authentication, String... roles) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        User user = (User) authentication.getPrincipal();
        if (user != null && Boolean.TRUE.equals(user.getIsSuperAdmin())) {
            return true;
        }

        if (user == null || user.getRoles() == null) {
            return false;
        }

        for (String role : roles) {
            if (user.getRoles().stream().anyMatch(r -> role.equalsIgnoreCase(r.getName()))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if user is super admin
     * Super admins bypass all permission/role checks
     *
     * @param authentication Spring Security authentication
     * @return true if user is super admin
     */
    public boolean isSuperAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        User user = (User) authentication.getPrincipal();
        return user != null && Boolean.TRUE.equals(user.getIsSuperAdmin());
    }

    /**
     * Internal helper: check if authentication has permission
     * Looks at user's roles and their permissions
     *
     * @param authentication Spring Security authentication
     * @param permission permission code
     * @return true if user has permission
     */
    private boolean hasPermissionInAuthentication(Authentication authentication, String permission) {
        User user = (User) authentication.getPrincipal();

        if (user == null || user.getRoles() == null || user.getRoles().isEmpty()) {
            return false;
        }

        // Check: any role has this permission
        return user.getRoles().stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(perm -> perm.getCode())
            .anyMatch(code -> code.equalsIgnoreCase(permission));
    }
}

