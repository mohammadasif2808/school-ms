@error-handling
Feature: Error Handling
  As an API consumer
  I want consistent error responses
  So that I can handle errors appropriately

  Background:
    # Database starts EMPTY - no pre-existing data
    # Each scenario tests specific error conditions

  # ============ STANDARD ERROR FORMAT SCENARIOS ============

  @smoke
  Scenario: Error responses have standard format
    When I attempt to sign in with username "nonexistent" and password "wrongpass"
    Then I should receive an unauthorized error
    And the response should contain field "error"
    And the response should contain field "message"

  @positive
  Scenario: Validation errors include details
    When I attempt to register with password "short"
    Then I should receive a bad request error
    And the response should contain field "error"

  # ============ HTTP STATUS CODE SCENARIOS ============

  Scenario Outline: Correct HTTP status codes are returned
    # Various error conditions should return appropriate status codes
    # 400 - Bad Request (validation errors)
    # 401 - Unauthorized (authentication required)
    # 403 - Forbidden (insufficient permissions)
    # 404 - Not Found
    # 409 - Conflict (duplicate resource)

    Examples:
      | scenario            | expected_status |
      | validation_error    | 400             |
      | unauthorized        | 401             |
      | conflict            | 409             |

  # ============ SECURITY - NO INFORMATION LEAKAGE ============

  @security
  Scenario: Error responses do not leak sensitive information
    When I attempt to sign in with username "admin" and password "wrongpassword"
    Then the response should not contain stack trace
    And the response should not expose sensitive information

  @security
  Scenario: Error responses do not reveal internal paths
    When I attempt to register with invalid email "invalid"
    Then the response should not contain stack trace
    And the response should not expose sensitive information

  @security
  Scenario: 404 errors do not reveal system information
    Given a new user "testUser" is registered
    And user "testUser" is authenticated
    # Accessing non-existent resource should return clean 404
    # without revealing internal details

  # ============ VALIDATION ERROR SCENARIOS ============

  @validation
  Scenario: Missing required fields return validation error
    When I attempt to sign in with username "" and password ""
    Then I should receive a bad request error

  @validation
  Scenario: Invalid format returns validation error
    When I attempt to register with invalid email "not-an-email"
    Then I should receive a bad request error
    And the error code should be "VALIDATION_ERROR"

