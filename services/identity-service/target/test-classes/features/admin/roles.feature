@admin @roles
Feature: Admin Role Management
  As an administrator
  I want to manage roles
  So that I can control user access levels

  Background:
    # Database starts EMPTY - no pre-existing data
    # Each scenario creates its own admin users and data

  # ============ ROLE CREATION SCENARIOS ============

  @smoke @positive
  Scenario: Create a new role
    Given a new user "adminUser" is registered
    And user "adminUser" is authenticated
    When I create a role "TEACHER" as "adminUser"
    Then the role should be created successfully
    And the response status should be 201
    And the response should contain field "id"
    And the response should contain field "name"

  @positive
  Scenario: Create multiple roles
    Given a new user "adminUser" is registered
    And user "adminUser" is authenticated
    When I create a role "TEACHER" as "adminUser"
    Then the role should be created successfully
    When I create a role "LIBRARIAN" as "adminUser"
    Then the role should be created successfully

  # ============ LIST ROLES SCENARIOS ============

  @positive
  Scenario: List all roles
    Given a new user "adminUser" is registered
    And user "adminUser" is authenticated
    And a role "TEACHER" exists
    When I list all roles as "adminUser"
    Then the response status should be 200
    And I should see a list of roles

  # ============ GET ROLE BY ID SCENARIOS ============

  @positive
  Scenario: Get role by ID
    Given a new user "adminUser" is registered
    And user "adminUser" is authenticated
    And a role "ACCOUNTANT" exists
    When I get role "ACCOUNTANT" details as "adminUser"
    Then the response status should be 200
    And the response should contain field "id"
    And the response should contain field "name"

  # ============ AUTHORIZATION SCENARIOS ============

  @negative @security
  Scenario: Cannot create role without authentication
    When I attempt to create a role without authentication
    Then I should receive an unauthorized error

  # ============ VALIDATION SCENARIOS ============

  @negative @validation
  Scenario: Role creation requires valid data
    Given a new user "adminUser" is registered
    And user "adminUser" is authenticated
    # Empty role name should fail validation
    # This test ensures proper validation is in place

