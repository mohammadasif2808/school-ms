package com.school.identity.testutil;

import com.school.identity.domain.*;
import com.school.identity.dto.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Factory for creating test data objects
 * Provides consistent test data across all test classes
 */
public final class TestDataFactory {

    private TestDataFactory() {
        // Utility class - no instantiation
    }

    // ============ USER CREATION ============

    public static User createActiveUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPhone("+1234567890");
        user.setPasswordHash("$2a$10$hashedPasswordHere");
        user.setStatus(UserStatus.ACTIVE);
        user.setIsSuperAdmin(false);
        user.setIsDeleted(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setRoles(new HashSet<>());
        return user;
    }

    public static User createActiveUserWithId(UUID id) {
        User user = createActiveUser();
        user.setId(id);
        return user;
    }

    public static User createActiveUserWithUsername(String username) {
        User user = createActiveUser();
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        return user;
    }

    public static User createSuperAdmin() {
        User user = createActiveUser();
        user.setUsername("superadmin");
        user.setEmail("superadmin@example.com");
        user.setIsSuperAdmin(true);
        return user;
    }

    public static User createInactiveUser() {
        User user = createActiveUser();
        user.setUsername("inactiveuser");
        user.setEmail("inactive@example.com");
        user.setStatus(UserStatus.INACTIVE);
        return user;
    }

    public static User createBlockedUser() {
        User user = createActiveUser();
        user.setUsername("blockeduser");
        user.setEmail("blocked@example.com");
        user.setStatus(UserStatus.BLOCKED);
        return user;
    }

    public static User createDeletedUser() {
        User user = createActiveUser();
        user.setUsername("deleteduser");
        user.setEmail("deleted@example.com");
        user.setIsDeleted(true);
        return user;
    }

    public static User createUserWithRole(Role role) {
        User user = createActiveUser();
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        return user;
    }

    public static User createUserWithPermissions(String... permissionCodes) {
        Role role = createRoleWithPermissions(permissionCodes);
        return createUserWithRole(role);
    }

    // ============ ROLE CREATION ============

    public static Role createRole() {
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName("TEACHER");
        role.setDescription("Teacher role");
        role.setStatus(RoleStatus.ACTIVE);
        role.setCreatedAt(LocalDateTime.now());
        role.setPermissions(new HashSet<>());
        return role;
    }

    public static Role createRoleWithName(String name) {
        Role role = createRole();
        role.setName(name);
        return role;
    }

    public static Role createRoleWithPermissions(String... permissionCodes) {
        Role role = createRole();
        Set<Permission> permissions = new HashSet<>();
        for (String code : permissionCodes) {
            permissions.add(createPermissionWithCode(code));
        }
        role.setPermissions(permissions);
        return role;
    }

    // ============ PERMISSION CREATION ============

    public static Permission createPermission() {
        Permission permission = new Permission();
        permission.setId(UUID.randomUUID());
        permission.setCode("STUDENT_VIEW");
        permission.setModule("STUDENT");
        permission.setDescription("View student data");
        permission.setCreatedAt(LocalDateTime.now());
        return permission;
    }

    public static Permission createPermissionWithCode(String code) {
        Permission permission = createPermission();
        permission.setCode(code);
        return permission;
    }

    public static Permission createPermissionWithCodeAndModule(String code, String module) {
        Permission permission = createPermission();
        permission.setCode(code);
        permission.setModule(module);
        return permission;
    }

    // ============ PASSWORD RESET TOKEN CREATION ============

    public static PasswordResetToken createValidResetToken(User user) {
        PasswordResetToken token = new PasswordResetToken();
        token.setId(UUID.randomUUID());
        token.setUser(user);
        token.setToken("valid-reset-token-" + UUID.randomUUID());
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        token.setIsUsed(false);
        token.setCreatedAt(LocalDateTime.now());
        return token;
    }

    public static PasswordResetToken createExpiredResetToken(User user) {
        PasswordResetToken token = createValidResetToken(user);
        token.setExpiresAt(LocalDateTime.now().minusHours(1));
        return token;
    }

    public static PasswordResetToken createUsedResetToken(User user) {
        PasswordResetToken token = createValidResetToken(user);
        token.setIsUsed(true);
        token.setUsedAt(LocalDateTime.now().minusHours(1));
        return token;
    }

    // ============ REQUEST DTO CREATION ============

    public static SignUpRequest createValidSignUpRequest() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("newuser");
        request.setEmail("newuser@example.com");
        request.setPassword("SecureP@ss123");
        request.setFirst_name("New");
        request.setLast_name("User");
        request.setPhone("+1234567890");
        return request;
    }

    public static SignUpRequest createSignUpRequestWithUsername(String username) {
        SignUpRequest request = createValidSignUpRequest();
        request.setUsername(username);
        return request;
    }

    public static SignUpRequest createSignUpRequestWithEmail(String email) {
        SignUpRequest request = createValidSignUpRequest();
        request.setEmail(email);
        return request;
    }

    public static SignUpRequest createSignUpRequestWithPassword(String password) {
        SignUpRequest request = createValidSignUpRequest();
        request.setPassword(password);
        return request;
    }

    public static SignInRequest createValidSignInRequest() {
        SignInRequest request = new SignInRequest();
        request.setUsername("testuser");
        request.setPassword("SecureP@ss123");
        return request;
    }

    public static SignInRequest createSignInRequestWithCredentials(String username, String password) {
        SignInRequest request = new SignInRequest();
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }

    public static ForgotPasswordRequest createForgotPasswordRequest(String email) {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail(email);
        return request;
    }

    public static ResetPasswordRequest createResetPasswordRequest(String token, String newPassword) {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken(token);
        request.setNewPassword(newPassword);
        return request;
    }

    public static CreateRoleRequest createCreateRoleRequest(String name) {
        CreateRoleRequest request = new CreateRoleRequest();
        request.setName(name);
        request.setDescription("Test role description");
        return request;
    }

    public static CreatePermissionRequest createCreatePermissionRequest(String code, String module) {
        CreatePermissionRequest request = new CreatePermissionRequest();
        request.setCode(code);
        request.setModule(module);
        request.setDescription("Test permission description");
        return request;
    }

    public static AssignPermissionsRequest createAssignPermissionsRequest(String roleId, Set<String> permissionIds) {
        AssignPermissionsRequest request = new AssignPermissionsRequest();
        request.setRoleId(roleId);
        request.setPermissionIds(permissionIds);
        return request;
    }

    public static AssignRolesRequest createAssignRolesRequest(String userId, Set<String> roleIds) {
        AssignRolesRequest request = new AssignRolesRequest();
        request.setUserId(userId);
        request.setRoleIds(roleIds);
        return request;
    }

    // ============ JWT CLAIMS CREATION ============

    public static JwtClaims createValidJwtClaims() {
        JwtClaims claims = new JwtClaims();
        claims.setUserId(UUID.randomUUID());
        claims.setUsername("testuser");
        claims.setRole("TEACHER");
        claims.setPermissions(java.util.List.of("STUDENT_VIEW", "ATTENDANCE_MARK"));
        claims.setTenantId("school-001");
        claims.setIat(System.currentTimeMillis());
        claims.setExp(System.currentTimeMillis() + 86400000); // 24 hours
        return claims;
    }

    public static JwtClaims createExpiredJwtClaims() {
        JwtClaims claims = createValidJwtClaims();
        claims.setExp(System.currentTimeMillis() - 1000); // Expired 1 second ago
        return claims;
    }

    // ============ CONSTANTS ============

    public static final String VALID_PASSWORD = "SecureP@ss123";
    public static final String WEAK_PASSWORD_NO_UPPERCASE = "securep@ss123";
    public static final String WEAK_PASSWORD_NO_LOWERCASE = "SECUREP@SS123";
    public static final String WEAK_PASSWORD_NO_DIGIT = "SecureP@ssword";
    public static final String WEAK_PASSWORD_NO_SPECIAL = "SecurePass123";
    public static final String WEAK_PASSWORD_TOO_SHORT = "Sec@1";
    public static final String VALID_EMAIL = "test@example.com";
    public static final String INVALID_EMAIL = "invalid-email";
}

