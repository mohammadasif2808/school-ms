@authentication @profile
Feature: Get Current User Profile (/me)
  As an authenticated user
  I want to view my profile
  So that I can see my account details and permissions

  Background:
    # Database starts EMPTY - no pre-existing data
    # Each scenario creates and authenticates its own users

  # ============ HAPPY PATH SCENARIOS ============

  @smoke @positive
  Scenario: Get current user profile successfully
    Given a new user "testUser" is registered
    And user "testUser" is authenticated
    When I request my profile as "testUser"
    Then the response status should be 200
    And the response should contain my user profile
    And the response should contain field "id"
    And the response should contain field "username"
    And the response should contain field "email"
    And the response should contain field "status"

  @positive
  Scenario: Profile contains permissions list
    Given a new user "testUser" is registered
    And user "testUser" is authenticated
    When I request my profile as "testUser"
    Then the response status should be 200
    And the profile should contain my permissions

  @positive
  Scenario: Profile does not expose sensitive data
    Given a new user "testUser" is registered
    And user "testUser" is authenticated
    When I request my profile as "testUser"
    Then the response status should be 200
    And the response should not contain password
    And the response should not expose sensitive information

  # ============ AUTHENTICATION REQUIRED SCENARIOS ============

  @negative @security
  Scenario: Cannot access profile without authentication
    When I request my profile without authentication
    Then I should receive an unauthorized error
    And the error code should be "UNAUTHORIZED"

  @negative @security
  Scenario: Cannot access profile with invalid token
    When I request my profile with invalid token
    Then I should receive an unauthorized error

  @negative @security
  Scenario: Cannot access profile with malformed token
    When I request my profile with invalid token
    Then I should receive an unauthorized error

  # ============ TOKEN VALIDATION SCENARIOS ============

  @security
  Scenario: Valid token allows profile access
    Given a new user "testUser" is registered
    And user "testUser" is authenticated
    When I request my profile as "testUser"
    Then the response status should be 200
    And the response should contain my user profile

  @security
  Scenario: Response does not leak internal details on error
    When I request my profile with invalid token
    Then I should receive an unauthorized error
    And the response should not contain stack trace

