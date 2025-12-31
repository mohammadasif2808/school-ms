package com.school.identity.bdd.steps;

import com.school.identity.bdd.client.IdentityApiClient;
import com.school.identity.bdd.context.ScenarioContext;
import com.school.identity.bdd.util.TestDataGenerator;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Authentication Step Definitions
 *
 * Handles signup, signin, signout, and /me endpoint steps
 */
public class AuthenticationSteps {

    private final ScenarioContext context;
    private final IdentityApiClient apiClient;

    public AuthenticationSteps(ScenarioContext context, IdentityApiClient apiClient) {
        this.context = context;
        this.apiClient = apiClient;
    }

    // ============ USER CREATION (GIVEN) ============

    @Given("a new user {string} is registered")
    public void aNewUserIsRegistered(String userAlias) {
        TestDataGenerator.SignupData data = TestDataGenerator.generateSignupData();

        Response response = apiClient.signUp(
            data.username, data.email, data.password,
            data.firstName, data.lastName, data.phone
        );

        assertThat(response.getStatusCode())
            .as("User registration should succeed")
            .isEqualTo(201);

        // Store credentials for later use
        context.storeUserCredentials(userAlias, data.username, data.password, data.email);

        // Store user ID if returned
        UUID userId = apiClient.extractUserId(response);
        if (userId != null) {
            context.storeCreatedUserId(userAlias, userId);
        }

        context.setLastResponse(response);
    }

    @Given("a new user {string} is registered with username {string}")
    public void aNewUserIsRegisteredWithUsername(String userAlias, String username) {
        String email = TestDataGenerator.generateEmail(username);
        String password = TestDataGenerator.generateSecurePassword();
        String phone = TestDataGenerator.generatePhone();

        Response response = apiClient.signUp(
            username, email, password,
            "Test", "User", phone
        );

        assertThat(response.getStatusCode())
            .as("User registration should succeed")
            .isEqualTo(201);

        context.storeUserCredentials(userAlias, username, password, email);

        UUID userId = apiClient.extractUserId(response);
        if (userId != null) {
            context.storeCreatedUserId(userAlias, userId);
        }

        context.setLastResponse(response);
    }

    @Given("a new user {string} is registered with email {string}")
    public void aNewUserIsRegisteredWithEmail(String userAlias, String email) {
        String username = TestDataGenerator.generateUsername();
        String password = TestDataGenerator.generateSecurePassword();
        String phone = TestDataGenerator.generatePhone();

        Response response = apiClient.signUp(
            username, email, password,
            "Test", "User", phone
        );

        assertThat(response.getStatusCode())
            .as("User registration should succeed")
            .isEqualTo(201);

        context.storeUserCredentials(userAlias, username, password, email);

        UUID userId = apiClient.extractUserId(response);
        if (userId != null) {
            context.storeCreatedUserId(userAlias, userId);
        }

        context.setLastResponse(response);
    }

    @Given("user {string} is authenticated")
    public void userIsAuthenticated(String userAlias) {
        ScenarioContext.UserCredentials creds = context.getUserCredentials(userAlias);

        Response response = apiClient.signIn(creds.getUsername(), creds.getPassword());

        assertThat(response.getStatusCode())
            .as("User signin should succeed")
            .isEqualTo(200);

        String token = apiClient.extractToken(response);
        assertThat(token)
            .as("JWT token should be returned")
            .isNotBlank();

        context.storeJwtToken(userAlias, token);
        context.setLastResponse(response);
    }

    // ============ SIGNUP (WHEN) ============

    @When("I attempt to register a new user")
    public void iAttemptToRegisterANewUser() {
        TestDataGenerator.SignupData data = TestDataGenerator.generateSignupData();

        Response response = apiClient.signUp(
            data.username, data.email, data.password,
            data.firstName, data.lastName, data.phone
        );

        context.storeUserCredentials("lastAttempted", data.username, data.password, data.email);
        context.setLastResponse(response);
    }

    @When("I attempt to register with username {string}")
    public void iAttemptToRegisterWithUsername(String username) {
        String email = TestDataGenerator.generateEmail();
        String password = TestDataGenerator.generateSecurePassword();
        String phone = TestDataGenerator.generatePhone();

        Response response = apiClient.signUp(
            username, email, password,
            "Test", "User", phone
        );

        context.setLastResponse(response);
    }

    @When("I attempt to register with the same username as {string}")
    public void iAttemptToRegisterWithSameUsernameAs(String existingUserAlias) {
        ScenarioContext.UserCredentials existingCreds = context.getUserCredentials(existingUserAlias);

        String newEmail = TestDataGenerator.generateEmail();
        String password = TestDataGenerator.generateSecurePassword();
        String phone = TestDataGenerator.generatePhone();

        Response response = apiClient.signUp(
            existingCreds.getUsername(), newEmail, password,
            "Test", "User", phone
        );

        context.setLastResponse(response);
    }

    @When("I attempt to register with the same email as {string}")
    public void iAttemptToRegisterWithSameEmailAs(String existingUserAlias) {
        ScenarioContext.UserCredentials existingCreds = context.getUserCredentials(existingUserAlias);

        String newUsername = TestDataGenerator.generateUsername();
        String password = TestDataGenerator.generateSecurePassword();
        String phone = TestDataGenerator.generatePhone();

        Response response = apiClient.signUp(
            newUsername, existingCreds.getEmail(), password,
            "Test", "User", phone
        );

        context.setLastResponse(response);
    }

    @When("I attempt to register with password {string}")
    public void iAttemptToRegisterWithPassword(String password) {
        String username = TestDataGenerator.generateUsername();
        String email = TestDataGenerator.generateEmail();
        String phone = TestDataGenerator.generatePhone();

        Response response = apiClient.signUp(
            username, email, password,
            "Test", "User", phone
        );

        context.setLastResponse(response);
    }

    @When("I attempt to register with invalid email {string}")
    public void iAttemptToRegisterWithInvalidEmail(String invalidEmail) {
        String username = TestDataGenerator.generateUsername();
        String password = TestDataGenerator.generateSecurePassword();
        String phone = TestDataGenerator.generatePhone();

        Response response = apiClient.signUp(
            username, invalidEmail, password,
            "Test", "User", phone
        );

        context.setLastResponse(response);
    }

    // ============ SIGNIN (WHEN) ============

    @When("I sign in as {string}")
    public void iSignInAs(String userAlias) {
        ScenarioContext.UserCredentials creds = context.getUserCredentials(userAlias);

        Response response = apiClient.signIn(creds.getUsername(), creds.getPassword());

        if (response.getStatusCode() == 200) {
            String token = apiClient.extractToken(response);
            context.storeJwtToken(userAlias, token);
        }

        context.setLastResponse(response);
    }

    @When("I attempt to sign in with username {string} and password {string}")
    public void iAttemptToSignInWithCredentials(String username, String password) {
        Response response = apiClient.signIn(username, password);
        context.setLastResponse(response);
    }

    @When("I attempt to sign in as {string} with wrong password")
    public void iAttemptToSignInWithWrongPassword(String userAlias) {
        ScenarioContext.UserCredentials creds = context.getUserCredentials(userAlias);

        Response response = apiClient.signIn(creds.getUsername(), "WrongPassword123!");
        context.setLastResponse(response);
    }

    // ============ SIGNOUT (WHEN) ============

    @When("I sign out as {string}")
    public void iSignOutAs(String userAlias) {
        String token = context.getJwtToken(userAlias);
        Response response = apiClient.signOut(token);
        context.setLastResponse(response);
    }

    // ============ GET CURRENT USER (WHEN) ============

    @When("I request my profile as {string}")
    public void iRequestMyProfileAs(String userAlias) {
        String token = context.getJwtToken(userAlias);
        Response response = apiClient.getCurrentUser(token);
        context.setLastResponse(response);
    }

    @When("I request my profile without authentication")
    public void iRequestMyProfileWithoutAuthentication() {
        Response response = apiClient.getCurrentUserWithoutToken();
        context.setLastResponse(response);
    }

    @When("I request my profile with invalid token")
    public void iRequestMyProfileWithInvalidToken() {
        Response response = apiClient.getCurrentUser("invalid.jwt.token");
        context.setLastResponse(response);
    }

    // ============ ASSERTIONS (THEN) ============

    @Then("the registration should succeed")
    public void theRegistrationShouldSucceed() {
        assertThat(context.getLastStatusCode())
            .as("Registration should return 201 Created")
            .isEqualTo(201);
    }

    @Then("the signin should succeed")
    public void theSigninShouldSucceed() {
        assertThat(context.getLastStatusCode())
            .as("Signin should return 200 OK")
            .isEqualTo(200);
    }

    @Then("the signout should succeed")
    public void theSignoutShouldSucceed() {
        assertThat(context.getLastStatusCode())
            .as("Signout should return 200 OK")
            .isEqualTo(200);
    }

    @Then("I should receive a JWT token")
    public void iShouldReceiveAJwtToken() {
        Response response = context.getLastResponse();
        String token = response.jsonPath().getString("accessToken");

        assertThat(token)
            .as("Response should contain accessToken")
            .isNotBlank();

        // JWT tokens have 3 parts separated by dots
        assertThat(token.split("\\."))
            .as("Token should be valid JWT format (3 parts)")
            .hasSize(3);
    }

    @Then("the response should contain my user profile")
    public void theResponseShouldContainMyUserProfile() {
        Response response = context.getLastResponse();

        assertThat(response.getStatusCode())
            .as("Profile request should succeed")
            .isEqualTo(200);

        assertThat(response.jsonPath().getString("username"))
            .as("Response should contain username")
            .isNotBlank();

        assertThat(response.jsonPath().getString("email"))
            .as("Response should contain email")
            .isNotBlank();
    }

    @Then("the profile should contain my permissions")
    public void theProfileShouldContainMyPermissions() {
        Response response = context.getLastResponse();

        // permissions field should exist (may be empty list)
        assertThat(response.jsonPath().getList("permissions"))
            .as("Response should contain permissions array")
            .isNotNull();
    }

    @And("the response should not contain password")
    public void theResponseShouldNotContainPassword() {
        String responseBody = context.getLastResponseBody();

        assertThat(responseBody)
            .as("Response should not contain password field")
            .doesNotContain("password")
            .doesNotContain("password_hash");
    }
}

