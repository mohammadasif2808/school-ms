@authentication @signin
Feature: User Authentication (Sign In)
  As a registered user
  I want to sign in to my account
  So that I can access protected resources

  Background:
    # Database starts EMPTY - no pre-existing data
    # Each scenario creates its own test data via signup API

  # ============ HAPPY PATH SCENARIOS ============

  @smoke @positive
  Scenario: Successful sign in with valid credentials
    Given a new user "testUser" is registered
    When I sign in as "testUser"
    Then the signin should succeed
    And the response status should be 200
    And I should receive a JWT token
    And the response should contain field "accessToken"
    And the response should contain field "user.username"

  @positive
  Scenario: JWT token contains required claims
    Given a new user "testUser" is registered
    When I sign in as "testUser"
    Then the signin should succeed
    And I should receive a JWT token

  @positive
  Scenario: User can access protected endpoint after signin
    Given a new user "testUser" is registered
    And user "testUser" is authenticated
    When I request my profile as "testUser"
    Then the response status should be 200
    And the response should contain my user profile

  # ============ INVALID CREDENTIALS SCENARIOS ============

  @negative @security
  Scenario: Sign in fails with wrong password
    Given a new user "testUser" is registered
    When I attempt to sign in as "testUser" with wrong password
    Then I should receive an unauthorized error
    And the error code should be "INVALID_CREDENTIALS"

  @negative @security
  Scenario: Sign in fails with non-existent username
    When I attempt to sign in with username "nonexistent_user_xyz" and password "SomePass123!"
    Then I should receive an unauthorized error
    And the error code should be "INVALID_CREDENTIALS"

  @negative @security
  Scenario: Error message does not reveal if username exists
    When I attempt to sign in with username "nonexistent_user_abc" and password "WrongPass!"
    Then I should receive an unauthorized error
    And the error code should be "INVALID_CREDENTIALS"
    And the error message should contain "Invalid"
    # Message should NOT say "user not found" - prevents username enumeration

  # ============ VALIDATION SCENARIOS ============

  @negative @validation
  Scenario: Sign in fails with empty username
    When I attempt to sign in with username "" and password "SomePassword123!"
    Then I should receive a bad request error

  @negative @validation
  Scenario: Sign in fails with empty password
    Given a new user "testUser" is registered
    When I attempt to sign in with username "testUser" and password ""
    Then I should receive a bad request error

  # ============ SECURITY SCENARIOS ============

  @security
  Scenario: Password is never returned in signin response
    Given a new user "testUser" is registered
    When I sign in as "testUser"
    Then the signin should succeed
    And the response should not contain field "password"
    And the response should not expose sensitive information

  @security
  Scenario: Failed signin does not expose sensitive information
    When I attempt to sign in with username "fakeuser" and password "wrongpass"
    Then I should receive an unauthorized error
    And the response should not contain stack trace
    And the response should not expose sensitive information

