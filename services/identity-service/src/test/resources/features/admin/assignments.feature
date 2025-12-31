@admin @rbac @assignments
Feature: Role and Permission Assignments
  As an administrator
  I want to assign permissions to roles and roles to users
  So that users gain appropriate access rights

  Background:
    # Database starts EMPTY - no pre-existing data
    # Each scenario creates its own data via APIs

  # ============ PERMISSION TO ROLE ASSIGNMENT ============

  @smoke @positive
  Scenario: Assign permission to role
    Given a new user "adminUser" is registered
    And user "adminUser" is authenticated
    And a role "TEACHER" exists
    And a permission "STUDENT_VIEW" exists in module "STUDENT"
    When I assign permission "STUDENT_VIEW" to role "TEACHER" as "adminUser"
    Then the permission assignment should succeed
    And the response status should be 200

  @positive
  Scenario: Assign multiple permissions to role
    Given a new user "adminUser" is registered
    And user "adminUser" is authenticated
    And a role "TEACHER" exists
    And a permission "STUDENT_VIEW" exists in module "STUDENT"
    And a permission "ATTENDANCE_MARK" exists in module "ATTENDANCE"
    When I assign permissions "STUDENT_VIEW" and "ATTENDANCE_MARK" to role "TEACHER" as "adminUser"
    Then the permission assignment should succeed

  # ============ ROLE TO USER ASSIGNMENT ============

  @smoke @positive
  Scenario: Assign role to user
    Given a new user "adminUser" is registered
    And user "adminUser" is authenticated
    And a new user "teacherUser" is registered
    And a role "TEACHER" exists
    When I assign role "TEACHER" to user "teacherUser" as "adminUser"
    Then the role assignment should succeed
    And the response status should be 200

  @positive
  Scenario: User gains permissions through role assignment
    Given a new user "adminUser" is registered
    And user "adminUser" is authenticated
    And a new user "teacherUser" is registered
    And a role "TEACHER" exists
    And a permission "STUDENT_VIEW" exists in module "STUDENT"
    When I assign permission "STUDENT_VIEW" to role "TEACHER" as "adminUser"
    Then the permission assignment should succeed
    When I assign role "TEACHER" to user "teacherUser" as "adminUser"
    Then the role assignment should succeed

  # ============ RBAC VERIFICATION SCENARIOS ============

  @positive @rbac
  Scenario: User with role can be verified
    Given a new user "adminUser" is registered
    And user "adminUser" is authenticated
    And a new user "teacherUser" is registered
    And a role "TEACHER" exists
    When I assign role "TEACHER" to user "teacherUser" as "adminUser"
    Then the role assignment should succeed
    # Teacher can now sign in and should have role in token
    When user "teacherUser" is authenticated
    And I request my profile as "teacherUser"
    Then the response status should be 200

  # ============ AUTHORIZATION SCENARIOS ============

  @negative @security
  Scenario: Non-admin cannot assign roles
    # First create an admin and a regular user
    Given a new user "adminUser" is registered
    And user "adminUser" is authenticated
    And a new user "regularUser" is registered
    And user "regularUser" is authenticated
    And a new user "targetUser" is registered
    And a role "TEACHER" exists
    # Regular user tries to assign role (should fail without proper permissions)
    # Note: This depends on whether regularUser has ROLE_MANAGE permission

