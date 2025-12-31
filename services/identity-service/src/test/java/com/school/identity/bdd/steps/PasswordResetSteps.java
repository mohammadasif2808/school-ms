package com.school.identity.bdd.steps;

import com.school.identity.bdd.client.IdentityApiClient;
import com.school.identity.bdd.context.ScenarioContext;
import com.school.identity.bdd.util.TestDataGenerator;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Password Reset Step Definitions
 *
 * Handles forgot-password and reset-password flows
 */
public class PasswordResetSteps {

    private final ScenarioContext context;
    private final IdentityApiClient apiClient;

    public PasswordResetSteps(ScenarioContext context, IdentityApiClient apiClient) {
        this.context = context;
        this.apiClient = apiClient;
    }

    // ============ FORGOT PASSWORD (WHEN) ============

    @When("I request password reset for {string}")
    public void iRequestPasswordResetFor(String userAlias) {
        ScenarioContext.UserCredentials creds = context.getUserCredentials(userAlias);

        Response response = apiClient.forgotPassword(creds.getEmail());

        context.setPasswordResetEmail(creds.getEmail());
        context.setLastResponse(response);
    }

    @When("I request password reset for email {string}")
    public void iRequestPasswordResetForEmail(String email) {
        Response response = apiClient.forgotPassword(email);

        context.setPasswordResetEmail(email);
        context.setLastResponse(response);
    }

    @When("I request password reset for non-existent email {string}")
    public void iRequestPasswordResetForNonExistentEmail(String email) {
        Response response = apiClient.forgotPassword(email);

        context.setLastResponse(response);
    }

    // ============ RESET PASSWORD (WHEN) ============

    @When("I reset password with token {string} and new password {string}")
    public void iResetPasswordWithTokenAndNewPassword(String token, String newPassword) {
        Response response = apiClient.resetPassword(token, newPassword);
        context.setLastResponse(response);
    }

    @When("I reset password with valid token and new password {string}")
    public void iResetPasswordWithValidTokenAndNewPassword(String newPassword) {
        String token = context.getPasswordResetToken();

        assertThat(token)
            .as("Password reset token should be available")
            .isNotBlank();

        Response response = apiClient.resetPassword(token, newPassword);
        context.setLastResponse(response);
    }

    @When("I reset password with expired token")
    public void iResetPasswordWithExpiredToken() {
        Response response = apiClient.resetPassword(
            "expired-token-12345",
            TestDataGenerator.generateSecurePassword()
        );
        context.setLastResponse(response);
    }

    @When("I reset password with invalid token")
    public void iResetPasswordWithInvalidToken() {
        Response response = apiClient.resetPassword(
            "invalid-token-xyz",
            TestDataGenerator.generateSecurePassword()
        );
        context.setLastResponse(response);
    }

    @When("I reset password with empty token")
    public void iResetPasswordWithEmptyToken() {
        Response response = apiClient.resetPassword(
            "",
            TestDataGenerator.generateSecurePassword()
        );
        context.setLastResponse(response);
    }

    @When("I reset password with weak password {string}")
    public void iResetPasswordWithWeakPassword(String weakPassword) {
        String token = context.getPasswordResetToken();

        // Use a dummy token if no real token available
        String tokenToUse = (token != null) ? token : "test-token";

        Response response = apiClient.resetPassword(tokenToUse, weakPassword);
        context.setLastResponse(response);
    }

    // ============ ASSERTIONS (THEN) ============

    @Then("the password reset request should succeed")
    public void thePasswordResetRequestShouldSucceed() {
        // Password reset always returns 200 for security (no email enumeration)
        assertThat(context.getLastStatusCode())
            .as("Forgot password should return 200 OK")
            .isEqualTo(200);
    }

    @Then("the response should not reveal if email exists")
    public void theResponseShouldNotRevealIfEmailExists() {
        // Both existing and non-existing emails should return same response
        assertThat(context.getLastStatusCode())
            .as("Should return 200 regardless of email existence")
            .isEqualTo(200);

        Response response = context.getLastResponse();
        String message = response.jsonPath().getString("message");

        // Message should be generic
        assertThat(message)
            .as("Message should be generic (no email enumeration)")
            .doesNotContain("not found")
            .doesNotContain("does not exist");
    }

    @Then("the password should be reset successfully")
    public void thePasswordShouldBeResetSuccessfully() {
        assertThat(context.getLastStatusCode())
            .as("Password reset should return 200 OK")
            .isEqualTo(200);
    }

    @Then("I should be able to sign in with the new password")
    public void iShouldBeAbleToSignInWithTheNewPassword() {
        // This step would be used after password reset
        // The calling scenario should store the new password
        Response response = context.getLastResponse();
        assertThat(response.getStatusCode())
            .as("Signin with new password should succeed")
            .isEqualTo(200);
    }

    @Then("the old password should no longer work")
    public void theOldPasswordShouldNoLongerWork() {
        Response response = context.getLastResponse();

        assertThat(response.getStatusCode())
            .as("Old password should be rejected (401)")
            .isEqualTo(401);
    }

    @Then("the reset token should be invalid after use")
    public void theResetTokenShouldBeInvalidAfterUse() {
        Response response = context.getLastResponse();

        // Using same token again should fail
        assertThat(response.getStatusCode())
            .as("Reused token should be rejected")
            .isIn(400, 404);
    }
}

