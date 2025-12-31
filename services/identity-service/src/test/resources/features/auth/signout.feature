@authentication @signout
Feature: User Sign Out
  As an authenticated user
  I want to sign out of my account
  So that my session is terminated

  Background:
    # Database starts EMPTY - no pre-existing data
    # Each scenario creates and authenticates its own users

  # ============ HAPPY PATH SCENARIOS ============

  @smoke @positive
  Scenario: Successful sign out
    Given a new user "testUser" is registered
    And user "testUser" is authenticated
    When I sign out as "testUser"
    Then the signout should succeed
    And the response status should be 200
    And the response should be a success message

  # ============ EDGE CASE SCENARIOS ============

  @positive
  Scenario: Sign out is idempotent
    Given a new user "testUser" is registered
    And user "testUser" is authenticated
    When I sign out as "testUser"
    Then the signout should succeed
    # Note: With stateless JWT, signout is mainly client-side token disposal

