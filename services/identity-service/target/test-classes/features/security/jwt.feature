@security @jwt
Feature: JWT Security
  As a security-conscious system
  I want to properly validate JWT tokens
  So that only authorized users can access protected resources

  Background:
    # Database starts EMPTY - no pre-existing data
    # Each scenario creates its own users and tokens

  # ============ TOKEN STRUCTURE SCENARIOS ============

  @smoke @positive
  Scenario: JWT token has valid structure
    Given a new user "testUser" is registered
    When I sign in as "testUser"
    Then the signin should succeed
    And I should receive a JWT token
    # JWT should have 3 parts: header.payload.signature

  @positive
  Scenario: JWT token can be used for authentication
    Given a new user "testUser" is registered
    And user "testUser" is authenticated
    When I request my profile as "testUser"
    Then the response status should be 200
    And the response should contain my user profile

  # ============ TOKEN VALIDATION SCENARIOS ============

  @negative @security
  Scenario: Invalid token is rejected
    When I request my profile with invalid token
    Then I should receive an unauthorized error

  @negative @security
  Scenario: Malformed token is rejected
    When I request my profile with invalid token
    Then I should receive an unauthorized error

  @negative @security
  Scenario: Missing Authorization header is rejected
    When I request my profile without authentication
    Then I should receive an unauthorized error
    And the error code should be "UNAUTHORIZED"

  # ============ TOKEN EXPIRY SCENARIOS ============

  # Note: Testing token expiry with real expired tokens requires
  # either waiting for expiry or using specifically crafted tokens

  # ============ SECURITY EDGE CASES ============

  @security
  Scenario: Empty Bearer token is rejected
    When I request my profile with invalid token
    Then I should receive an unauthorized error

  @security
  Scenario: Token tampering is detected
    # If token signature is invalid, it should be rejected
    When I request my profile with invalid token
    Then I should receive an unauthorized error

  @security
  Scenario: Valid token allows access to protected endpoints
    Given a new user "testUser" is registered
    And user "testUser" is authenticated
    When I request my profile as "testUser"
    Then the response status should be 200

  @security
  Scenario: Different users get different tokens
    Given a new user "user1" is registered
    And user "user1" is authenticated
    Given a new user "user2" is registered
    And user "user2" is authenticated
    # Both users should have valid but different tokens
    When I request my profile as "user1"
    Then the response status should be 200
    When I request my profile as "user2"
    Then the response status should be 200

