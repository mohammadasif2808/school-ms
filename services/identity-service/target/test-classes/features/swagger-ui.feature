@swagger-ui @ui-smoke
Feature: Swagger UI Smoke Tests
  As an API consumer
  I want to access Swagger UI
  So that I can explore and test the API

  Background:
    Given the identity-service is running

  # ============ SWAGGER UI ACCESSIBILITY ============

  @smoke
  Scenario: Swagger UI is accessible
    When I navigate to Swagger UI
    Then the Swagger UI page should load successfully

  @smoke
  Scenario: Swagger UI displays API information
    When I navigate to Swagger UI
    Then the Swagger UI page should load successfully
    And I should see the API title "Identity Service"

  @smoke
  Scenario: Swagger UI shows authentication endpoints
    When I navigate to Swagger UI
    Then the Swagger UI page should load successfully
    And I should see the Authentication endpoints
    And I should see HTTP methods for authentication

  # ============ SWAGGER JWT AUTHORIZATION ============

  @smoke
  Scenario: Swagger UI has JWT authorization support
    When I navigate to Swagger UI
    Then the Swagger UI page should load successfully
    When I click the Authorize button
    Then I should see the authorization dialog

  # ============ ENDPOINT VISIBILITY ============

  @smoke
  Scenario: Authentication endpoints are visible
    When I navigate to Swagger UI
    Then the Swagger UI page should load successfully
    And I should see endpoint "/auth/signup"
    And I should see endpoint "/auth/signin"

