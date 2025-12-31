package com.school.identity.bdd.client;

import com.school.identity.bdd.config.RestAssuredConfig;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;

/**
 * API Client for identity-service
 *
 * Encapsulates all API calls with proper request/response handling.
 * Each method returns the raw Response for verification in step definitions.
 */
public class IdentityApiClient {

    // ============ AUTHENTICATION ENDPOINTS ============

    /**
     * POST /api/v1/auth/signup
     * Creates a new user account
     */
    public Response signUp(String username, String email, String password,
                          String firstName, String lastName, String phone) {
        Map<String, Object> requestBody = Map.of(
            "username", username,
            "email", email,
            "password", password,
            "first_name", firstName,
            "last_name", lastName,
            "phone", phone
        );

        return given()
            .spec(RestAssuredConfig.getBaseSpec())
            .body(requestBody)
            .when()
            .post("/auth/signup");
    }

    /**
     * POST /api/v1/auth/signin
     * Authenticates user and returns JWT token
     */
    public Response signIn(String username, String password) {
        Map<String, Object> requestBody = Map.of(
            "username", username,
            "password", password
        );

        return given()
            .spec(RestAssuredConfig.getBaseSpec())
            .body(requestBody)
            .when()
            .post("/auth/signin");
    }

    /**
     * POST /api/v1/auth/signout
     * Signs out the authenticated user
     */
    public Response signOut(String jwtToken) {
        return given()
            .spec(RestAssuredConfig.getAuthenticatedSpec(jwtToken))
            .when()
            .post("/auth/signout");
    }

    /**
     * GET /api/v1/auth/me
     * Gets current authenticated user profile
     */
    public Response getCurrentUser(String jwtToken) {
        return given()
            .spec(RestAssuredConfig.getAuthenticatedSpec(jwtToken))
            .when()
            .get("/auth/me");
    }

    /**
     * GET /api/v1/auth/me - without token (for negative testing)
     */
    public Response getCurrentUserWithoutToken() {
        return given()
            .spec(RestAssuredConfig.getBaseSpec())
            .when()
            .get("/auth/me");
    }

    /**
     * POST /api/v1/auth/forgot-password
     * Initiates password reset flow
     */
    public Response forgotPassword(String email) {
        Map<String, Object> requestBody = Map.of("email", email);

        return given()
            .spec(RestAssuredConfig.getBaseSpec())
            .body(requestBody)
            .when()
            .post("/auth/forgot-password");
    }

    /**
     * POST /api/v1/auth/reset-password
     * Completes password reset with token
     */
    public Response resetPassword(String token, String newPassword) {
        Map<String, Object> requestBody = Map.of(
            "token", token,
            "newPassword", newPassword
        );

        return given()
            .spec(RestAssuredConfig.getBaseSpec())
            .body(requestBody)
            .when()
            .post("/auth/reset-password");
    }

    // ============ ADMIN - ROLE ENDPOINTS ============

    /**
     * POST /api/v1/admin/roles
     * Creates a new role (requires admin token)
     */
    public Response createRole(String jwtToken, String name, String description) {
        Map<String, Object> requestBody = Map.of(
            "name", name,
            "description", description
        );

        return given()
            .spec(RestAssuredConfig.getAuthenticatedSpec(jwtToken))
            .body(requestBody)
            .when()
            .post("/admin/roles");
    }

    /**
     * GET /api/v1/admin/roles
     * Lists all roles
     */
    public Response listRoles(String jwtToken) {
        return given()
            .spec(RestAssuredConfig.getAuthenticatedSpec(jwtToken))
            .when()
            .get("/admin/roles");
    }

    /**
     * GET /api/v1/admin/roles/{roleId}
     * Gets role by ID
     */
    public Response getRoleById(String jwtToken, UUID roleId) {
        return given()
            .spec(RestAssuredConfig.getAuthenticatedSpec(jwtToken))
            .when()
            .get("/admin/roles/" + roleId);
    }

    // ============ ADMIN - PERMISSION ENDPOINTS ============

    /**
     * POST /api/v1/admin/permissions
     * Creates a new permission (requires admin token)
     */
    public Response createPermission(String jwtToken, String code, String module, String description) {
        Map<String, Object> requestBody = Map.of(
            "code", code,
            "module", module,
            "description", description
        );

        return given()
            .spec(RestAssuredConfig.getAuthenticatedSpec(jwtToken))
            .body(requestBody)
            .when()
            .post("/admin/permissions");
    }

    /**
     * GET /api/v1/admin/permissions
     * Lists all permissions
     */
    public Response listPermissions(String jwtToken) {
        return given()
            .spec(RestAssuredConfig.getAuthenticatedSpec(jwtToken))
            .when()
            .get("/admin/permissions");
    }

    // ============ ADMIN - ROLE-PERMISSION ASSIGNMENT ============

    /**
     * POST /api/v1/admin/roles/{roleId}/permissions
     * Assigns permissions to a role
     */
    public Response assignPermissionsToRole(String jwtToken, UUID roleId, List<UUID> permissionIds) {
        Map<String, Object> requestBody = Map.of(
            "roleId", roleId.toString(),
            "permissionIds", permissionIds.stream().map(UUID::toString).toList()
        );

        return given()
            .spec(RestAssuredConfig.getAuthenticatedSpec(jwtToken))
            .body(requestBody)
            .when()
            .post("/admin/roles/" + roleId + "/permissions");
    }

    // ============ ADMIN - USER-ROLE ASSIGNMENT ============

    /**
     * POST /api/v1/admin/users/{userId}/roles
     * Assigns roles to a user
     */
    public Response assignRolesToUser(String jwtToken, UUID userId, List<UUID> roleIds) {
        Map<String, Object> requestBody = Map.of(
            "userId", userId.toString(),
            "roleIds", roleIds.stream().map(UUID::toString).toList()
        );

        return given()
            .spec(RestAssuredConfig.getAuthenticatedSpec(jwtToken))
            .body(requestBody)
            .when()
            .post("/admin/users/" + userId + "/roles");
    }

    // ============ HELPER METHODS ============

    /**
     * Extract JWT token from signin response
     */
    public String extractToken(Response signInResponse) {
        return signInResponse.jsonPath().getString("accessToken");
    }

    /**
     * Extract user ID from response
     */
    public UUID extractUserId(Response response) {
        String id = response.jsonPath().getString("id");
        if (id == null) {
            id = response.jsonPath().getString("user.id");
        }
        return id != null ? UUID.fromString(id) : null;
    }

    /**
     * Extract role ID from response
     */
    public UUID extractRoleId(Response response) {
        String id = response.jsonPath().getString("id");
        return id != null ? UUID.fromString(id) : null;
    }

    /**
     * Extract permission ID from response
     */
    public UUID extractPermissionId(Response response) {
        String id = response.jsonPath().getString("id");
        return id != null ? UUID.fromString(id) : null;
    }
}

