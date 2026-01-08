@authentication @password-reset
Feature: Password Reset Flow
  As a user who forgot their password
  I want to reset my password
  So that I can regain access to my account

  Background:
    # Database starts EMPTY - no pre-existing data
    # Each scenario creates its own users via signup API

  # ============ FORGOT PASSWORD SCENARIOS ============

  @smoke @positive
  Scenario: Request password reset for existing user
    Given a new user "testUser" is registered
    When I request password reset for "testUser"
    Then the password reset request should succeed
    And the response status should be 200

  @security @positive
  Scenario: Password reset request for non-existent email returns same response
    When I request password reset for non-existent email "nobody@example.com"
    Then the password reset request should succeed
    And the response should not reveal if email exists
    # SECURITY: Prevents email enumeration attacks

  @security
  Scenario: Password reset request does not reveal user existence
    Given a new user "existingUser" is registered
    # Request for existing email
    When I request password reset for "existingUser"
    Then the response status should be 200
    # Request for non-existing email should return same type of response
    When I request password reset for email "nonexistent@test.com"
    Then the response status should be 200
    And the response should not reveal if email exists

  # ============ RESET PASSWORD SCENARIOS ============

  @negative
  Scenario: Reset password with invalid token fails
    When I reset password with invalid token
    Then I should receive a bad request error

  @negative
  Scenario: Reset password with empty token fails
    When I reset password with empty token
    Then I should receive a bad request error

  @negative @validation
  Scenario: Reset password with weak password fails
    When I reset password with token "some-token" and new password "weak"
    Then I should receive a bad request error

  # ============ TOKEN SECURITY SCENARIOS ============

  @security
  Scenario: Reset token format validation
    When I reset password with token "invalid-format-token-123" and new password "NewSecure@Pass123"
    Then the request should be rejected with 400

  @security
  Scenario: Expired token is rejected
    When I reset password with expired token
    Then I should receive a bad request error

  # ============ PASSWORD VALIDATION SCENARIOS ============

  @negative @validation
  Scenario Outline: Reset password validation
    When I reset password with token "test-token" and new password "<password>"
    Then I should receive a bad request error

    Examples:
      | password | reason           |
      | short    | Too short        |
      | 12345678 | No letters       |

