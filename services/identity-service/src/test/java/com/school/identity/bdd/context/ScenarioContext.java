package com.school.identity.bdd.context;

import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Scenario Context - Holds state shared across steps in a single scenario
 *
 * This is crucial for:
 * - Storing dynamically created user credentials
 * - Passing JWT tokens between steps
 * - Tracking created resources for cleanup
 *
 * THREAD-SAFE: Each scenario gets its own instance
 */
public class ScenarioContext {

    // ============ RESPONSE STORAGE ============
    private Response lastResponse;
    private int lastStatusCode;
    private String lastResponseBody;

    // ============ USER CREDENTIALS ============
    // Stores users created during this scenario
    // Key: logical name (e.g., "testUser", "adminUser")
    // Value: UserCredentials containing username, password, email
    private final Map<String, UserCredentials> createdUsers = new HashMap<>();

    // ============ JWT TOKENS ============
    // Stores JWT tokens obtained via signin
    // Key: logical name (e.g., "testUser", "adminUser")
    // Value: JWT token string
    private final Map<String, String> jwtTokens = new HashMap<>();

    // ============ CREATED RESOURCES ============
    // Stores IDs of created resources for verification/cleanup
    private final Map<String, UUID> createdRoles = new HashMap<>();
    private final Map<String, UUID> createdPermissions = new HashMap<>();
    private final Map<String, UUID> createdUserIds = new HashMap<>();

    // ============ PASSWORD RESET ============
    private String passwordResetToken;
    private String passwordResetEmail;

    // ============ RESPONSE METHODS ============

    public void setLastResponse(Response response) {
        this.lastResponse = response;
        this.lastStatusCode = response.getStatusCode();
        this.lastResponseBody = response.getBody().asString();
    }

    public Response getLastResponse() {
        return lastResponse;
    }

    public int getLastStatusCode() {
        return lastStatusCode;
    }

    public String getLastResponseBody() {
        return lastResponseBody;
    }

    // ============ USER CREDENTIAL METHODS ============

    public void storeUserCredentials(String alias, String username, String password, String email) {
        createdUsers.put(alias, new UserCredentials(username, password, email));
    }

    public UserCredentials getUserCredentials(String alias) {
        UserCredentials creds = createdUsers.get(alias);
        if (creds == null) {
            throw new IllegalStateException("No user credentials found for alias: " + alias +
                ". Available aliases: " + createdUsers.keySet());
        }
        return creds;
    }

    public boolean hasUserCredentials(String alias) {
        return createdUsers.containsKey(alias);
    }

    // ============ JWT TOKEN METHODS ============

    public void storeJwtToken(String alias, String token) {
        jwtTokens.put(alias, token);
    }

    public String getJwtToken(String alias) {
        String token = jwtTokens.get(alias);
        if (token == null) {
            throw new IllegalStateException("No JWT token found for alias: " + alias +
                ". Available aliases: " + jwtTokens.keySet());
        }
        return token;
    }

    public boolean hasJwtToken(String alias) {
        return jwtTokens.containsKey(alias);
    }

    // ============ ROLE METHODS ============

    public void storeCreatedRole(String name, UUID id) {
        createdRoles.put(name, id);
    }

    public UUID getCreatedRoleId(String name) {
        UUID id = createdRoles.get(name);
        if (id == null) {
            throw new IllegalStateException("No role found with name: " + name +
                ". Available roles: " + createdRoles.keySet());
        }
        return id;
    }

    public boolean hasCreatedRole(String name) {
        return createdRoles.containsKey(name);
    }

    // ============ PERMISSION METHODS ============

    public void storeCreatedPermission(String code, UUID id) {
        createdPermissions.put(code, id);
    }

    public UUID getCreatedPermissionId(String code) {
        UUID id = createdPermissions.get(code);
        if (id == null) {
            throw new IllegalStateException("No permission found with code: " + code +
                ". Available permissions: " + createdPermissions.keySet());
        }
        return id;
    }

    public boolean hasCreatedPermission(String code) {
        return createdPermissions.containsKey(code);
    }

    // ============ USER ID METHODS ============

    public void storeCreatedUserId(String alias, UUID id) {
        createdUserIds.put(alias, id);
    }

    public UUID getCreatedUserId(String alias) {
        UUID id = createdUserIds.get(alias);
        if (id == null) {
            throw new IllegalStateException("No user ID found for alias: " + alias +
                ". Available user IDs: " + createdUserIds.keySet());
        }
        return id;
    }

    // ============ PASSWORD RESET METHODS ============

    public void setPasswordResetToken(String token) {
        this.passwordResetToken = token;
    }

    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    public void setPasswordResetEmail(String email) {
        this.passwordResetEmail = email;
    }

    public String getPasswordResetEmail() {
        return passwordResetEmail;
    }

    // ============ CLEANUP ============

    public void clear() {
        lastResponse = null;
        lastStatusCode = 0;
        lastResponseBody = null;
        createdUsers.clear();
        jwtTokens.clear();
        createdRoles.clear();
        createdPermissions.clear();
        createdUserIds.clear();
        passwordResetToken = null;
        passwordResetEmail = null;
    }

    // ============ INNER CLASSES ============

    public static class UserCredentials {
        private final String username;
        private final String password;
        private final String email;

        public UserCredentials(String username, String password, String email) {
            this.username = username;
            this.password = password;
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getEmail() {
            return email;
        }

        @Override
        public String toString() {
            return "UserCredentials{username='" + username + "', email='" + email + "'}";
        }
    }
}

