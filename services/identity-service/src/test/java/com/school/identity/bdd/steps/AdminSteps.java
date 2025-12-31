package com.school.identity.bdd.steps;

import com.school.identity.bdd.client.IdentityApiClient;
import com.school.identity.bdd.context.ScenarioContext;
import com.school.identity.bdd.util.TestDataGenerator;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.restassured.response.Response;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Admin API Step Definitions
 *
 * Handles role management, permission management, and assignments
 */
public class AdminSteps {

    private final ScenarioContext context;
    private final IdentityApiClient apiClient;

    public AdminSteps(ScenarioContext context, IdentityApiClient apiClient) {
        this.context = context;
        this.apiClient = apiClient;
    }

    // ============ ROLE MANAGEMENT (GIVEN/WHEN) ============

    @Given("a role {string} exists")
    public void aRoleExists(String roleName) {
        // This requires an authenticated admin user
        // We assume super admin or create one if needed
        ensureAdminAuthenticated();

        String token = getAdminToken();
        String uniqueRoleName = roleName + "_" + System.currentTimeMillis();

        Response response = apiClient.createRole(token, uniqueRoleName, "Test role: " + roleName);

        assertThat(response.getStatusCode())
            .as("Role creation should succeed")
            .isEqualTo(201);

        UUID roleId = apiClient.extractRoleId(response);
        context.storeCreatedRole(roleName, roleId);
        context.setLastResponse(response);
    }

    @When("I create a role {string} as {string}")
    public void iCreateARoleAs(String roleName, String userAlias) {
        String token = context.getJwtToken(userAlias);
        String uniqueRoleName = roleName + "_" + System.currentTimeMillis();

        Response response = apiClient.createRole(token, uniqueRoleName, "Test role: " + roleName);

        if (response.getStatusCode() == 201) {
            UUID roleId = apiClient.extractRoleId(response);
            context.storeCreatedRole(roleName, roleId);
        }

        context.setLastResponse(response);
    }

    @When("I attempt to create a role without authentication")
    public void iAttemptToCreateARoleWithoutAuthentication() {
        Response response = apiClient.createRole(
            "invalid-token",
            TestDataGenerator.generateRoleName(),
            "Test role"
        );
        context.setLastResponse(response);
    }

    @When("I list all roles as {string}")
    public void iListAllRolesAs(String userAlias) {
        String token = context.getJwtToken(userAlias);
        Response response = apiClient.listRoles(token);
        context.setLastResponse(response);
    }

    @When("I get role {string} details as {string}")
    public void iGetRoleDetailsAs(String roleName, String userAlias) {
        String token = context.getJwtToken(userAlias);
        UUID roleId = context.getCreatedRoleId(roleName);

        Response response = apiClient.getRoleById(token, roleId);
        context.setLastResponse(response);
    }

    // ============ PERMISSION MANAGEMENT (GIVEN/WHEN) ============

    @Given("a permission {string} exists in module {string}")
    public void aPermissionExistsInModule(String permCode, String module) {
        ensureAdminAuthenticated();

        String token = getAdminToken();
        String uniqueCode = permCode + "_" + System.currentTimeMillis();

        Response response = apiClient.createPermission(
            token, uniqueCode, module, "Test permission: " + permCode
        );

        assertThat(response.getStatusCode())
            .as("Permission creation should succeed")
            .isEqualTo(201);

        UUID permId = apiClient.extractPermissionId(response);
        context.storeCreatedPermission(permCode, permId);
        context.setLastResponse(response);
    }

    @When("I create a permission {string} in module {string} as {string}")
    public void iCreateAPermissionAs(String permCode, String module, String userAlias) {
        String token = context.getJwtToken(userAlias);
        String uniqueCode = permCode + "_" + System.currentTimeMillis();

        Response response = apiClient.createPermission(
            token, uniqueCode, module, "Test permission: " + permCode
        );

        if (response.getStatusCode() == 201) {
            UUID permId = apiClient.extractPermissionId(response);
            context.storeCreatedPermission(permCode, permId);
        }

        context.setLastResponse(response);
    }

    @When("I list all permissions as {string}")
    public void iListAllPermissionsAs(String userAlias) {
        String token = context.getJwtToken(userAlias);
        Response response = apiClient.listPermissions(token);
        context.setLastResponse(response);
    }

    // ============ ROLE-PERMISSION ASSIGNMENT (WHEN) ============

    @When("I assign permission {string} to role {string} as {string}")
    public void iAssignPermissionToRoleAs(String permCode, String roleName, String userAlias) {
        String token = context.getJwtToken(userAlias);
        UUID roleId = context.getCreatedRoleId(roleName);
        UUID permId = context.getCreatedPermissionId(permCode);

        Response response = apiClient.assignPermissionsToRole(
            token, roleId, List.of(permId)
        );

        context.setLastResponse(response);
    }

    @When("I assign permissions {string} and {string} to role {string} as {string}")
    public void iAssignMultiplePermissionsToRoleAs(String perm1, String perm2, String roleName, String userAlias) {
        String token = context.getJwtToken(userAlias);
        UUID roleId = context.getCreatedRoleId(roleName);
        UUID permId1 = context.getCreatedPermissionId(perm1);
        UUID permId2 = context.getCreatedPermissionId(perm2);

        Response response = apiClient.assignPermissionsToRole(
            token, roleId, List.of(permId1, permId2)
        );

        context.setLastResponse(response);
    }

    // ============ USER-ROLE ASSIGNMENT (WHEN) ============

    @When("I assign role {string} to user {string} as {string}")
    public void iAssignRoleToUserAs(String roleName, String targetUserAlias, String adminAlias) {
        String token = context.getJwtToken(adminAlias);
        UUID roleId = context.getCreatedRoleId(roleName);
        UUID userId = context.getCreatedUserId(targetUserAlias);

        Response response = apiClient.assignRolesToUser(
            token, userId, List.of(roleId)
        );

        context.setLastResponse(response);
    }

    // ============ ASSERTIONS (THEN) ============

    @Then("the role should be created successfully")
    public void theRoleShouldBeCreatedSuccessfully() {
        assertThat(context.getLastStatusCode())
            .as("Role creation should return 201 Created")
            .isEqualTo(201);

        assertThat(context.getLastResponse().jsonPath().getString("id"))
            .as("Response should contain role ID")
            .isNotBlank();
    }

    @Then("the permission should be created successfully")
    public void thePermissionShouldBeCreatedSuccessfully() {
        assertThat(context.getLastStatusCode())
            .as("Permission creation should return 201 Created")
            .isEqualTo(201);

        assertThat(context.getLastResponse().jsonPath().getString("id"))
            .as("Response should contain permission ID")
            .isNotBlank();
    }

    @Then("the permission assignment should succeed")
    public void thePermissionAssignmentShouldSucceed() {
        assertThat(context.getLastStatusCode())
            .as("Permission assignment should return 200 OK")
            .isEqualTo(200);
    }

    @Then("the role assignment should succeed")
    public void theRoleAssignmentShouldSucceed() {
        assertThat(context.getLastStatusCode())
            .as("Role assignment should return 200 OK")
            .isEqualTo(200);
    }

    @Then("I should see a list of roles")
    public void iShouldSeeAListOfRoles() {
        assertThat(context.getLastStatusCode())
            .as("List roles should return 200 OK")
            .isEqualTo(200);

        assertThat(context.getLastResponse().jsonPath().getList("$"))
            .as("Response should be a list")
            .isNotNull();
    }

    @Then("I should see a list of permissions")
    public void iShouldSeeAListOfPermissions() {
        assertThat(context.getLastStatusCode())
            .as("List permissions should return 200 OK")
            .isEqualTo(200);

        assertThat(context.getLastResponse().jsonPath().getList("$"))
            .as("Response should be a list")
            .isNotNull();
    }

    // ============ HELPER METHODS ============

    private void ensureAdminAuthenticated() {
        if (!context.hasJwtToken("superAdmin")) {
            // Create and authenticate a super admin
            // Note: In real scenario, super admin should exist or be created via special mechanism
            // For testing, we'll create a regular user and assume super admin privileges

            TestDataGenerator.SignupData data = TestDataGenerator.generateAdminSignupData();

            Response signupResponse = apiClient.signUp(
                data.username, data.email, data.password,
                data.firstName, data.lastName, data.phone
            );

            // If signup succeeds, sign in
            if (signupResponse.getStatusCode() == 201) {
                context.storeUserCredentials("superAdmin", data.username, data.password, data.email);

                UUID userId = apiClient.extractUserId(signupResponse);
                if (userId != null) {
                    context.storeCreatedUserId("superAdmin", userId);
                }

                Response signinResponse = apiClient.signIn(data.username, data.password);
                if (signinResponse.getStatusCode() == 200) {
                    String token = apiClient.extractToken(signinResponse);
                    context.storeJwtToken("superAdmin", token);
                }
            }
        }
    }

    private String getAdminToken() {
        return context.getJwtToken("superAdmin");
    }
}

