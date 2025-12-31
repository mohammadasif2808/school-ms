package com.school.identity.service;

import com.school.identity.domain.Permission;
import com.school.identity.domain.Role;
import com.school.identity.domain.RoleStatus;
import com.school.identity.domain.User;
import com.school.identity.dto.AssignPermissionsRequest;
import com.school.identity.dto.AssignRolesRequest;
import com.school.identity.dto.CreatePermissionRequest;
import com.school.identity.dto.CreateRoleRequest;
import com.school.identity.dto.PermissionResponse;
import com.school.identity.dto.RoleResponse;
import com.school.identity.exception.ValidationException;
import com.school.identity.repository.PermissionRepository;
import com.school.identity.repository.RoleRepository;
import com.school.identity.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for admin operations on roles and permissions
 *
 * Handles:
 * - Creating roles
 * - Creating permissions
 * - Assigning permissions to roles
 * - Assigning roles to users
 * - Listing roles and permissions
 */
@Service
public class AdminService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    public AdminService(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
    }

    // ============ ROLE OPERATIONS ============

    /**
     * Create a new role
     *
     * @param request CreateRoleRequest with role name and description
     * @return RoleResponse with created role
     * @throws ValidationException if role name already exists
     */
    @Transactional
    public RoleResponse createRole(CreateRoleRequest request) {
        String roleName = request.getName();

        // Check if role already exists
        if (roleRepository.existsByName(roleName)) {
            throw new ValidationException(
                "ROLE_EXISTS",
                "Role with name '" + roleName + "' already exists"
            );
        }

        // Create role
        Role role = new Role();
        role.setName(roleName);
        role.setDescription(request.getDescription());
        role.setStatus(RoleStatus.ACTIVE);
        role.setCreatedBy("SYSTEM");  // Could be current user

        // Save role
        Role savedRole = roleRepository.save(role);

        // Return response
        return mapToRoleResponse(savedRole);
    }

    /**
     * Get all roles
     *
     * @return List of RoleResponse objects
     */
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
            .map(this::mapToRoleResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get role by ID
     *
     * @param roleId role ID
     * @return RoleResponse with role details
     * @throws ValidationException if role not found
     */
    public RoleResponse getRoleById(UUID roleId) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ValidationException(
                "ROLE_NOT_FOUND",
                "Role not found with ID: " + roleId
            ));

        return mapToRoleResponse(role);
    }

    // ============ PERMISSION OPERATIONS ============

    /**
     * Create a new permission
     *
     * @param request CreatePermissionRequest with code, module, and description
     * @return PermissionResponse with created permission
     * @throws ValidationException if permission code already exists
     */
    @Transactional
    public PermissionResponse createPermission(CreatePermissionRequest request) {
        String permissionCode = request.getCode();

        // Check if permission already exists
        Optional<Permission> existingPerm = permissionRepository.findByCode(permissionCode);
        if (existingPerm.isPresent()) {
            throw new ValidationException(
                "PERMISSION_EXISTS",
                "Permission with code '" + permissionCode + "' already exists"
            );
        }

        // Create permission
        Permission permission = new Permission();
        permission.setCode(permissionCode);
        permission.setModule(request.getModule());
        permission.setDescription(request.getDescription());

        // Save permission
        Permission savedPermission = permissionRepository.save(permission);

        // Return response
        return mapToPermissionResponse(savedPermission);
    }

    /**
     * Get all permissions
     *
     * @return List of PermissionResponse objects
     */
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream()
            .map(this::mapToPermissionResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get permissions by module
     *
     * @param module module name
     * @return List of PermissionResponse objects for module
     */
    public List<PermissionResponse> getPermissionsByModule(String module) {
        return permissionRepository.findByModule(module).stream()
            .map(this::mapToPermissionResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get permission by ID
     *
     * @param permissionId permission ID
     * @return PermissionResponse with permission details
     * @throws ValidationException if permission not found
     */
    public PermissionResponse getPermissionById(UUID permissionId) {
        Permission permission = permissionRepository.findById(permissionId)
            .orElseThrow(() -> new ValidationException(
                "PERMISSION_NOT_FOUND",
                "Permission not found with ID: " + permissionId
            ));

        return mapToPermissionResponse(permission);
    }

    // ============ ROLE-PERMISSION ASSIGNMENT ============

    /**
     * Assign permissions to a role
     *
     * Replaces all existing permissions with the provided set
     *
     * @param request AssignPermissionsRequest with role ID and permission IDs
     * @return RoleResponse with updated role and permissions
     * @throws ValidationException if role or any permission not found
     */
    @Transactional
    public RoleResponse assignPermissionsToRole(AssignPermissionsRequest request) {
        UUID roleId = UUID.fromString(request.getRoleId());

        // Find role
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ValidationException(
                "ROLE_NOT_FOUND",
                "Role not found with ID: " + roleId
            ));

        // Find all permissions
        Set<UUID> permissionIds = request.getPermissionIds().stream()
            .map(UUID::fromString)
            .collect(Collectors.toSet());

        List<Permission> permissions = permissionRepository.findAllById(permissionIds);

        // Check if all permissions were found
        if (permissions.size() != permissionIds.size()) {
            throw new ValidationException(
                "PERMISSION_NOT_FOUND",
                "One or more permissions not found"
            );
        }

        // Assign permissions to role (replace existing)
        role.setPermissions(new java.util.HashSet<>(permissions));
        role.setUpdatedAt(LocalDateTime.now());

        // Save role
        Role updatedRole = roleRepository.save(role);

        // Return response
        return mapToRoleResponse(updatedRole);
    }

    // ============ USER-ROLE ASSIGNMENT ============

    /**
     * Assign roles to a user
     *
     * Replaces all existing roles with the provided set
     *
     * @param request AssignRolesRequest with user ID and role IDs
     * @return user with updated roles
     * @throws ValidationException if user or any role not found
     */
    @Transactional
    public void assignRolesToUser(AssignRolesRequest request) {
        UUID userId = UUID.fromString(request.getUserId());

        // Find user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ValidationException(
                "USER_NOT_FOUND",
                "User not found with ID: " + userId
            ));

        // Check user not deleted
        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new ValidationException(
                "USER_NOT_FOUND",
                "User not found (deleted)"
            );
        }

        // Find all roles
        Set<UUID> roleIds = request.getRoleIds().stream()
            .map(UUID::fromString)
            .collect(Collectors.toSet());

        List<Role> roles = roleRepository.findAllById(roleIds);

        // Check if all roles were found
        if (roles.size() != roleIds.size()) {
            throw new ValidationException(
                "ROLE_NOT_FOUND",
                "One or more roles not found"
            );
        }

        // Assign roles to user (replace existing)
        user.setRoles(new java.util.HashSet<>(roles));
        user.setLastModifiedAt(LocalDateTime.now());

        // Save user
        userRepository.save(user);
    }

    // ============ HELPER METHODS ============

    /**
     * Map Role entity to RoleResponse DTO
     */
    private RoleResponse mapToRoleResponse(Role role) {
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setName(role.getName());
        response.setDescription(role.getDescription());
        response.setStatus(role.getStatus().toString());
        response.setCreatedAt(role.getCreatedAt());
        response.setUpdatedAt(role.getUpdatedAt());

        // Map permissions
        if (role.getPermissions() != null && !role.getPermissions().isEmpty()) {
            Set<PermissionResponse> permissionResponses = role.getPermissions().stream()
                .map(this::mapToPermissionResponse)
                .collect(Collectors.toSet());
            response.setPermissions(permissionResponses);
        }

        return response;
    }

    /**
     * Map Permission entity to PermissionResponse DTO
     */
    private PermissionResponse mapToPermissionResponse(Permission permission) {
        return new PermissionResponse(
            permission.getId(),
            permission.getCode(),
            permission.getModule(),
            permission.getDescription(),
            permission.getCreatedAt()
        );
    }
}

