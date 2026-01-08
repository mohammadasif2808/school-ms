@authentication @signup
Feature: User Registration (Sign Up)
  As a new user
  I want to create an account
  So that I can access the system

  Background:
    # Database starts EMPTY - no pre-existing data
    # Each scenario creates its own test data

  # ============ HAPPY PATH SCENARIOS ============

  @smoke @positive
  Scenario: Successful user registration
    When I attempt to register a new user
    Then the registration should succeed
    And the response status should be 201
    And the response should contain field "id"
    And the response should contain field "username"
    And the response should contain field "email"
    And the response should contain field "status"
    And the response should not contain field "password"

  @positive
  Scenario: Registered user can sign in immediately
    Given a new user "testUser" is registered
    When I sign in as "testUser"
    Then the signin should succeed
    And I should receive a JWT token

  @positive
  Scenario: Registration returns correct user data
    When I attempt to register a new user
    Then the registration should succeed
    And the response should contain field "status" with value "ACTIVE"
    And the response should not expose sensitive information

  # ============ VALIDATION ERROR SCENARIOS ============

  @negative @validation
  Scenario: Registration fails with weak password (too short)
    When I attempt to register with password "short"
    Then I should receive a bad request error
    And the error code should be "VALIDATION_ERROR"

  @negative @validation
  Scenario: Registration fails with invalid email format
    When I attempt to register with invalid email "not-an-email"
    Then I should receive a bad request error
    And the error code should be "VALIDATION_ERROR"

  @negative @validation
  Scenario Outline: Registration fails with invalid password
    When I attempt to register with password "<password>"
    Then I should receive a bad request error
    And the error code should be "VALIDATION_ERROR"

    Examples:
      | password  | reason                    |
      | short     | Less than 8 characters    |
      | 12345678  | No letters or symbols     |
      | abcdefgh  | No numbers or symbols     |

  # ============ DUPLICATE USER SCENARIOS ============

  @negative @conflict
  Scenario: Registration fails with duplicate username
    Given a new user "existingUser" is registered
    When I attempt to register with the same username as "existingUser"
    Then I should receive a conflict error
    And the error code should be "USERNAME_EXISTS"

  @negative @conflict
  Scenario: Registration fails with duplicate email
    Given a new user "existingUser" is registered
    When I attempt to register with the same email as "existingUser"
    Then I should receive a conflict error
    And the error code should be "EMAIL_EXISTS"

  # ============ SECURITY SCENARIOS ============

  @security
  Scenario: Password is never returned in response
    When I attempt to register a new user
    Then the registration should succeed
    And the response should not contain field "password"
    And the response should not contain field "password_hash"
    And the response should not expose sensitive information

  @security
  Scenario: Registration response does not contain stack traces on error
    When I attempt to register with invalid email "invalid"
    Then the response should not contain stack trace

