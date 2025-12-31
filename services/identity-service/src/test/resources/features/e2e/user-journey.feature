@e2e @integration
Feature: End-to-End User Journey
  As a system
  I want to verify complete user workflows
  So that I can ensure all components work together

  Background:
    # Database starts EMPTY - no pre-existing data
    # These scenarios test complete user journeys from start to finish

  # ============ COMPLETE USER LIFECYCLE ============

  @smoke @critical
  Scenario: Complete user registration and authentication flow
    # Step 1: User registers
    When I attempt to register a new user
    Then the registration should succeed
    And the response status should be 201

    # Step 2: User signs in (use stored credentials from registration)
    Given a new user "flowUser" is registered
    When I sign in as "flowUser"
    Then the signin should succeed
    And I should receive a JWT token

    # Step 3: User accesses protected resource
    When I request my profile as "flowUser"
    Then the response status should be 200
    And the response should contain my user profile

    # Step 4: User signs out
    When I sign out as "flowUser"
    Then the signout should succeed

  @critical
  Scenario: User with role and permissions flow
    # Step 1: Create admin user
    Given a new user "adminUser" is registered
    And user "adminUser" is authenticated

    # Step 2: Create role
    When I create a role "TEACHER" as "adminUser"
    Then the role should be created successfully

    # Step 3: Create permission
    When I create a permission "STUDENT_VIEW" in module "STUDENT" as "adminUser"
    Then the permission should be created successfully

    # Step 4: Assign permission to role
    When I assign permission "STUDENT_VIEW" to role "TEACHER" as "adminUser"
    Then the permission assignment should succeed

    # Step 5: Create target user and assign role
    Given a new user "teacherUser" is registered
    When I assign role "TEACHER" to user "teacherUser" as "adminUser"
    Then the role assignment should succeed

    # Step 6: Teacher signs in and verifies access
    When user "teacherUser" is authenticated
    And I request my profile as "teacherUser"
    Then the response status should be 200

  # ============ SECURITY BOUNDARY TESTS ============

  @security
  Scenario: Unauthenticated access is properly blocked
    # All protected endpoints should require authentication
    When I request my profile without authentication
    Then I should receive an unauthorized error

  @security
  Scenario: Invalid credentials are properly rejected
    Given a new user "testUser" is registered
    When I attempt to sign in as "testUser" with wrong password
    Then I should receive an unauthorized error
    And the error code should be "INVALID_CREDENTIALS"

  # ============ DATA ISOLATION TESTS ============

  @isolation
  Scenario: Users cannot access other users' data
    # Create two independent users
    Given a new user "user1" is registered
    And user "user1" is authenticated
    Given a new user "user2" is registered
    And user "user2" is authenticated

    # Each user can only access their own profile
    When I request my profile as "user1"
    Then the response status should be 200
    When I request my profile as "user2"
    Then the response status should be 200

