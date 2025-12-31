package com.school.identity.controller;

import com.school.identity.dto.AssignPermissionsRequest;
import com.school.identity.dto.AssignRolesRequest;
import com.school.identity.dto.CreatePermissionRequest;
import com.school.identity.dto.CreateRoleRequest;
import com.school.identity.dto.PermissionResponse;
import com.school.identity.dto.RoleResponse;
import com.school.identity.exception.ValidationException;
import com.school.identity.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for admin operations (role and permission management)
 *
 * Protected API: /api/v1/admin/**
 *
 * Requires: ROLE_MANAGE or PERMISSION_MANAGE permissions
 * Or: Super admin status
 */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ============ ROLE ENDPOINTS ============

    /**
     * Create a new role
     *
     * POST /api/v1/admin/roles
     *
     * Requires: ROLE_MANAGE permission (or super admin)
     *
     * @param request CreateRoleRequest with role name and description
     * @return 201 Created with role details
     */
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'ROLE_MANAGE') OR " +
                  "@permissionEvaluator.isSuperAdmin(authentication)")
    @PostMapping("/roles")
    public ResponseEntity<?> createRole(@Valid @RequestBody CreateRoleRequest request) {
        try {
            RoleResponse response = adminService.createRole(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse(e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
        }
    }

    /**
     * List all roles
     *
     * GET /api/v1/admin/roles
     *
     * Requires: ROLE_VIEW permission (or super admin)
     *
     * @return 200 OK with list of roles
     */
    @PreAuthorize("@permissionEvaluator.hasAnyPermission(authentication, 'ROLE_VIEW', 'ROLE_MANAGE') OR " +
                  "@permissionEvaluator.isSuperAdmin(authentication)")
    @GetMapping("/roles")
    public ResponseEntity<?> listRoles() {
        try {
            List<RoleResponse> roles = adminService.getAllRoles();
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
        }
    }

    /**
     * Get role by ID
     *
     * GET /api/v1/admin/roles/{roleId}
     *
     * Requires: ROLE_VIEW permission (or super admin)
     *
     * @param roleId role ID
     * @return 200 OK with role details
     */
    @PreAuthorize("@permissionEvaluator.hasAnyPermission(authentication, 'ROLE_VIEW', 'ROLE_MANAGE') OR " +
                  "@permissionEvaluator.isSuperAdmin(authentication)")
    @GetMapping("/roles/{roleId}")
    public ResponseEntity<?> getRoleById(@PathVariable UUID roleId) {
        try {
            RoleResponse response = adminService.getRoleById(roleId);
            return ResponseEntity.ok(response);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse(e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
        }
    }

    // ============ PERMISSION ENDPOINTS ============

    /**
     * Create a new permission
     *
     * POST /api/v1/admin/permissions
     *
     * Requires: PERMISSION_MANAGE permission (or super admin)
     *
     * @param request CreatePermissionRequest with code, module, and description
     * @return 201 Created with permission details
     */
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'PERMISSION_MANAGE') OR " +
                  "@permissionEvaluator.isSuperAdmin(authentication)")
    @PostMapping("/permissions")
    public ResponseEntity<?> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        try {
            PermissionResponse response = adminService.createPermission(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse(e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
        }
    }

    /**
     * List all permissions
     *
     * GET /api/v1/admin/permissions
     *
     * Requires: PERMISSION_VIEW or PERMISSION_MANAGE permission (or super admin)
     *
     * @return 200 OK with list of permissions
     */
    @PreAuthorize("@permissionEvaluator.hasAnyPermission(authentication, 'PERMISSION_VIEW', 'PERMISSION_MANAGE') OR " +
                  "@permissionEvaluator.isSuperAdmin(authentication)")
    @GetMapping("/permissions")
    public ResponseEntity<?> listPermissions() {
        try {
            List<PermissionResponse> permissions = adminService.getAllPermissions();
            return ResponseEntity.ok(permissions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
        }
    }

    /**
     * Get permissions by module
     *
     * GET /api/v1/admin/permissions/module/{module}
     *
     * Requires: PERMISSION_VIEW or PERMISSION_MANAGE permission (or super admin)
     *
     * @param module module name
     * @return 200 OK with list of permissions for module
     */
    @PreAuthorize("@permissionEvaluator.hasAnyPermission(authentication, 'PERMISSION_VIEW', 'PERMISSION_MANAGE') OR " +
                  "@permissionEvaluator.isSuperAdmin(authentication)")
    @GetMapping("/permissions/module/{module}")
    public ResponseEntity<?> getPermissionsByModule(@PathVariable String module) {
        try {
            List<PermissionResponse> permissions = adminService.getPermissionsByModule(module);
            return ResponseEntity.ok(permissions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
        }
    }

    /**
     * Get permission by ID
     *
     * GET /api/v1/admin/permissions/{permissionId}
     *
     * Requires: PERMISSION_VIEW or PERMISSION_MANAGE permission (or super admin)
     *
     * @param permissionId permission ID
     * @return 200 OK with permission details
     */
    @PreAuthorize("@permissionEvaluator.hasAnyPermission(authentication, 'PERMISSION_VIEW', 'PERMISSION_MANAGE') OR " +
                  "@permissionEvaluator.isSuperAdmin(authentication)")
    @GetMapping("/permissions/{permissionId}")
    public ResponseEntity<?> getPermissionById(@PathVariable UUID permissionId) {
        try {
            PermissionResponse response = adminService.getPermissionById(permissionId);
            return ResponseEntity.ok(response);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse(e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
        }
    }

    // ============ ROLE-PERMISSION ASSIGNMENT ============

    /**
     * Assign permissions to a role
     *
     * POST /api/v1/admin/roles/{roleId}/permissions
     *
     * Requires: ROLE_MANAGE permission (or super admin)
     *
     * @param request AssignPermissionsRequest with permission IDs
     * @return 200 OK with updated role
     */
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'ROLE_MANAGE') OR " +
                  "@permissionEvaluator.isSuperAdmin(authentication)")
    @PostMapping("/roles/{roleId}/permissions")
    public ResponseEntity<?> assignPermissionsToRole(
            @PathVariable UUID roleId,
            @Valid @RequestBody AssignPermissionsRequest request) {
        try {
            // Override roleId from path
            request.setRoleId(roleId.toString());

            RoleResponse response = adminService.assignPermissionsToRole(request);
            return ResponseEntity.ok(response);
        } catch (ValidationException e) {
            if ("ROLE_NOT_FOUND".equals(e.getErrorCode())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getErrorCode(), e.getMessage()));
            }
            return ResponseEntity.badRequest()
                .body(createErrorResponse(e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
        }
    }

    // ============ USER-ROLE ASSIGNMENT ============

    /**
     * Assign roles to a user
     *
     * POST /api/v1/admin/users/{userId}/roles
     *
     * Requires: ROLE_MANAGE permission (or super admin)
     *
     * @param userId user ID
     * @param request AssignRolesRequest with role IDs
     * @return 200 OK with success message
     */
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'ROLE_MANAGE') OR " +
                  "@permissionEvaluator.isSuperAdmin(authentication)")
    @PostMapping("/users/{userId}/roles")
    public ResponseEntity<?> assignRolesToUser(
            @PathVariable UUID userId,
            @Valid @RequestBody AssignRolesRequest request) {
        try {
            // Override userId from path
            request.setUserId(userId.toString());

            adminService.assignRolesToUser(request);
            return ResponseEntity.ok(createMessageResponse("Roles assigned successfully"));
        } catch (ValidationException e) {
            if ("USER_NOT_FOUND".equals(e.getErrorCode())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getErrorCode(), e.getMessage()));
            }
            return ResponseEntity.badRequest()
                .body(createErrorResponse(e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
        }
    }

    // ============ HELPER METHODS ============

    /**
     * Create error response DTO
     */
    private Object createErrorResponse(String error, String message) {
        var errorResponse = new java.util.LinkedHashMap<String, Object>();
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        return errorResponse;
    }

    /**
     * Create message response DTO
     */
    private Object createMessageResponse(String message) {
        var response = new java.util.LinkedHashMap<String, String>();
        response.put("message", message);
        return response;
    }
}

