@admin @permissions
Feature: Admin Permission Management
  As an administrator
  I want to manage permissions
  So that I can define fine-grained access controls

  Background:
    # Database starts EMPTY - no pre-existing data
    # Each scenario creates its own admin users and data

  # ============ PERMISSION CREATION SCENARIOS ============

  @smoke @positive
  Scenario: Create a new permission
    Given a new user "adminUser" is registered
    And user "adminUser" is authenticated
    When I create a permission "STUDENT_VIEW" in module "STUDENT" as "adminUser"
    Then the permission should be created successfully
    And the response status should be 201
    And the response should contain field "id"
    And the response should contain field "code"
    And the response should contain field "module"

  @positive
  Scenario: Create permissions for different modules
    Given a new user "adminUser" is registered
    And user "adminUser" is authenticated
    When I create a permission "STUDENT_VIEW" in module "STUDENT" as "adminUser"
    Then the permission should be created successfully
    When I create a permission "ATTENDANCE_MARK" in module "ATTENDANCE" as "adminUser"
    Then the permission should be created successfully
    When I create a permission "FEE_COLLECT" in module "FINANCE" as "adminUser"
    Then the permission should be created successfully

  # ============ LIST PERMISSIONS SCENARIOS ============

  @positive
  Scenario: List all permissions
    Given a new user "adminUser" is registered
    And user "adminUser" is authenticated
    And a permission "STUDENT_VIEW" exists in module "STUDENT"
    When I list all permissions as "adminUser"
    Then the response status should be 200
    And I should see a list of permissions

  # ============ AUTHORIZATION SCENARIOS ============

  @negative @security
  Scenario: Cannot create permission without authentication
    # Trying to create permission without auth token should fail
    Given a new user "regularUser" is registered
    # Not authenticating - no token
    When I attempt to create a role without authentication
    Then I should receive an unauthorized error

