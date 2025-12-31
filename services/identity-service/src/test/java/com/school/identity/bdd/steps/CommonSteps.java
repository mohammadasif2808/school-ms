package com.school.identity.bdd.steps;

import com.school.identity.bdd.context.ScenarioContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Common Step Definitions
 *
 * Reusable steps for response validation and error handling
 */
public class CommonSteps {

    private final ScenarioContext context;

    public CommonSteps(ScenarioContext context) {
        this.context = context;
    }

    // ============ STATUS CODE ASSERTIONS ============

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        assertThat(context.getLastStatusCode())
            .as("Response status code")
            .isEqualTo(expectedStatus);
    }

    @Then("I should receive a {int} response")
    public void iShouldReceiveAResponse(int expectedStatus) {
        assertThat(context.getLastStatusCode())
            .as("Response status code")
            .isEqualTo(expectedStatus);
    }

    @Then("the request should be rejected with {int}")
    public void theRequestShouldBeRejectedWith(int expectedStatus) {
        assertThat(context.getLastStatusCode())
            .as("Response status code (rejected)")
            .isEqualTo(expectedStatus);
    }

    @Then("I should receive an unauthorized error")
    public void iShouldReceiveAnUnauthorizedError() {
        assertThat(context.getLastStatusCode())
            .as("Should return 401 Unauthorized")
            .isEqualTo(401);
    }

    @Then("I should receive a forbidden error")
    public void iShouldReceiveAForbiddenError() {
        assertThat(context.getLastStatusCode())
            .as("Should return 403 Forbidden")
            .isEqualTo(403);
    }

    @Then("I should receive a conflict error")
    public void iShouldReceiveAConflictError() {
        assertThat(context.getLastStatusCode())
            .as("Should return 409 Conflict")
            .isEqualTo(409);
    }

    @Then("I should receive a bad request error")
    public void iShouldReceiveABadRequestError() {
        assertThat(context.getLastStatusCode())
            .as("Should return 400 Bad Request")
            .isEqualTo(400);
    }

    @Then("I should receive a not found error")
    public void iShouldReceiveANotFoundError() {
        assertThat(context.getLastStatusCode())
            .as("Should return 404 Not Found")
            .isEqualTo(404);
    }

    // ============ ERROR RESPONSE ASSERTIONS ============

    @Then("the error code should be {string}")
    public void theErrorCodeShouldBe(String expectedErrorCode) {
        Response response = context.getLastResponse();
        String actualErrorCode = response.jsonPath().getString("error");

        assertThat(actualErrorCode)
            .as("Error code in response")
            .isEqualTo(expectedErrorCode);
    }

    @And("the error message should contain {string}")
    public void theErrorMessageShouldContain(String expectedText) {
        Response response = context.getLastResponse();
        String message = response.jsonPath().getString("message");

        assertThat(message)
            .as("Error message should contain expected text")
            .containsIgnoringCase(expectedText);
    }

    @Then("the response should contain error {string}")
    public void theResponseShouldContainError(String errorCode) {
        Response response = context.getLastResponse();
        String actualErrorCode = response.jsonPath().getString("error");

        assertThat(actualErrorCode)
            .as("Response should contain error code")
            .isEqualTo(errorCode);
    }

    // ============ RESPONSE BODY ASSERTIONS ============

    @Then("the response should contain field {string}")
    public void theResponseShouldContainField(String fieldName) {
        Response response = context.getLastResponse();
        Object fieldValue = response.jsonPath().get(fieldName);

        assertThat(fieldValue)
            .as("Response should contain field: " + fieldName)
            .isNotNull();
    }

    @Then("the response should contain field {string} with value {string}")
    public void theResponseShouldContainFieldWithValue(String fieldName, String expectedValue) {
        Response response = context.getLastResponse();
        String actualValue = response.jsonPath().getString(fieldName);

        assertThat(actualValue)
            .as("Field " + fieldName + " value")
            .isEqualTo(expectedValue);
    }

    @Then("the response should not contain field {string}")
    public void theResponseShouldNotContainField(String fieldName) {
        String responseBody = context.getLastResponseBody();

        assertThat(responseBody)
            .as("Response should not contain field: " + fieldName)
            .doesNotContain("\"" + fieldName + "\"");
    }

    @Then("the response should be a success message")
    public void theResponseShouldBeASuccessMessage() {
        Response response = context.getLastResponse();

        assertThat(context.getLastStatusCode())
            .as("Status code should be 200")
            .isEqualTo(200);

        assertThat(response.jsonPath().getString("message"))
            .as("Response should contain message field")
            .isNotBlank();
    }

    // ============ SECURITY ASSERTIONS ============

    @Then("the response should not expose sensitive information")
    public void theResponseShouldNotExposeSensitiveInformation() {
        String responseBody = context.getLastResponseBody().toLowerCase();

        assertThat(responseBody)
            .as("Response should not contain password")
            .doesNotContain("password")
            .doesNotContain("password_hash")
            .doesNotContain("secret");
    }

    @Then("the response should not contain stack trace")
    public void theResponseShouldNotContainStackTrace() {
        String responseBody = context.getLastResponseBody();

        assertThat(responseBody)
            .as("Response should not contain stack trace")
            .doesNotContain("at com.")
            .doesNotContain("java.lang.")
            .doesNotContain("Exception:");
    }

    // ============ TIMING/PERFORMANCE ASSERTIONS ============

    @Then("the response should be received within {int} milliseconds")
    public void theResponseShouldBeReceivedWithin(int maxMillis) {
        Response response = context.getLastResponse();
        long responseTime = response.getTime();

        assertThat(responseTime)
            .as("Response time should be within " + maxMillis + "ms")
            .isLessThanOrEqualTo(maxMillis);
    }
}

