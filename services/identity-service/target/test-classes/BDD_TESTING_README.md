# BDD API Testing Framework for Identity-Service

## Overview

This is a comprehensive **Behavior-Driven Development (BDD)** testing framework for the identity-service using:

- **Cucumber** (Gherkin syntax)
- **REST Assured** (API testing)
- **Selenium** (Swagger UI smoke tests only)
- **JUnit 5** (test runner)

## Key Design Principles

### 1. Empty Database Assumption
- Tests assume the database is **EMPTY** at start
- Each scenario creates its own test data via APIs
- No SQL seeding or hardcoded records

### 2. Dynamic Data Creation
- Users are created via `/api/v1/auth/signup`
- Roles/permissions are created via admin APIs
- JWT tokens are obtained dynamically via `/api/v1/auth/signin`

### 3. Test Isolation
- Each scenario uses a `ScenarioContext` for state management
- Context is cleared between scenarios
- Tests can run in any order

### 4. Token Reuse Pattern
```gherkin
Given a new user "testUser" is registered    # Creates user, stores credentials
And user "testUser" is authenticated          # Signs in, stores JWT token
When I request my profile as "testUser"       # Uses stored token
```

## Project Structure

```
src/test/
├── java/com/school/identity/bdd/
│   ├── CucumberTestRunner.java      # Test runner entry point
│   ├── client/
│   │   └── IdentityApiClient.java   # REST API client wrapper
│   ├── config/
│   │   ├── CucumberSpringConfig.java
│   │   └── RestAssuredConfig.java
│   ├── context/
│   │   └── ScenarioContext.java     # Shared state between steps
│   ├── hooks/
│   │   └── CucumberHooks.java       # Before/After hooks
│   ├── steps/
│   │   ├── AuthenticationSteps.java
│   │   ├── AdminSteps.java
│   │   ├── CommonSteps.java
│   │   ├── PasswordResetSteps.java
│   │   └── SwaggerUiSteps.java
│   └── util/
│       └── TestDataGenerator.java   # Unique data generation
└── resources/features/
    ├── auth/
    │   ├── signup.feature
    │   ├── signin.feature
    │   ├── signout.feature
    │   ├── me.feature
    │   └── password-reset.feature
    ├── admin/
    │   ├── roles.feature
    │   ├── permissions.feature
    │   └── assignments.feature
    ├── security/
    │   └── jwt.feature
    ├── e2e/
    │   └── user-journey.feature
    ├── error-handling.feature
    └── swagger-ui.feature
```

## Prerequisites

1. **Java 17+**
2. **Maven 3.6+**
3. **identity-service running on localhost:8080**
4. **Empty database** (or fresh schema)

## Running Tests

### Start the Service First
```bash
cd services/identity-service
mvn spring-boot:run
```

### Run All BDD Tests
```bash
mvn test -Dtest=CucumberTestRunner
```

### Run Specific Tags
```bash
# Run only smoke tests
mvn test -Dtest=CucumberTestRunner -Dcucumber.filter.tags="@smoke"

# Run authentication tests
mvn test -Dtest=CucumberTestRunner -Dcucumber.filter.tags="@authentication"

# Run security tests
mvn test -Dtest=CucumberTestRunner -Dcucumber.filter.tags="@security"

# Exclude Swagger UI tests (no browser needed)
mvn test -Dtest=CucumberTestRunner -Dcucumber.filter.tags="not @swagger-ui"
```

### Environment Variables
```bash
# Override service URL
export IDENTITY_SERVICE_URL=http://localhost:8080
```

## ScenarioContext Usage

The `ScenarioContext` stores state across steps within a single scenario:

### User Credentials
```java
// Store after signup
context.storeUserCredentials("myUser", username, password, email);

// Retrieve for signin
UserCredentials creds = context.getUserCredentials("myUser");
```

### JWT Tokens
```java
// Store after signin
context.storeJwtToken("myUser", jwtToken);

// Retrieve for API calls
String token = context.getJwtToken("myUser");
```

### Created Resources
```java
// Store role/permission IDs
context.storeCreatedRole("TEACHER", roleId);
context.storeCreatedPermission("STUDENT_VIEW", permId);

// Retrieve for assignments
UUID roleId = context.getCreatedRoleId("TEACHER");
```

## Test Data Generation

All test data is generated uniquely to avoid conflicts:

```java
// Each call returns unique values
String username = TestDataGenerator.generateUsername();      // "user_1704067200_1"
String email = TestDataGenerator.generateEmail();            // "test_1704067200_1@test.com"
String password = TestDataGenerator.generateSecurePassword(); // "Test@Pass1!"
```

## Feature Coverage

### Authentication
- ✅ Sign up (valid, invalid, duplicate)
- ✅ Sign in (valid, invalid, blocked)
- ✅ Sign out
- ✅ Get current user (/me)
- ✅ Password reset flow

### Authorization (RBAC)
- ✅ Role creation and listing
- ✅ Permission creation and listing
- ✅ Role-permission assignment
- ✅ User-role assignment

### Security
- ✅ JWT validation (valid, invalid, expired)
- ✅ Authorization header checks
- ✅ Protected endpoint access control
- ✅ No sensitive data exposure

### Error Handling
- ✅ Standard error format
- ✅ Proper HTTP status codes
- ✅ No stack trace exposure

### Swagger UI
- ✅ UI accessibility
- ✅ JWT authorization support
- ✅ Endpoint visibility

## Writing New Tests

### 1. Add Feature File
```gherkin
@my-feature
Feature: My New Feature
  
  Scenario: Test something new
    Given a new user "testUser" is registered
    And user "testUser" is authenticated
    When I do something as "testUser"
    Then something should happen
```

### 2. Add Step Definition
```java
@When("I do something as {string}")
public void iDoSomethingAs(String userAlias) {
    String token = context.getJwtToken(userAlias);
    Response response = apiClient.doSomething(token);
    context.setLastResponse(response);
}

@Then("something should happen")
public void somethingShouldHappen() {
    assertThat(context.getLastStatusCode()).isEqualTo(200);
}
```

## Reports

After running tests, reports are generated in:
- `target/cucumber-reports/cucumber.html` (HTML report)
- `target/cucumber-reports/cucumber.json` (JSON report)

## Troubleshooting

### Service Not Running
```
Connection refused: connect
```
**Solution:** Start identity-service first

### Database Not Empty
Tests may fail if previous data exists.
**Solution:** Clear database or use fresh schema

### Port Already in Use
```
Address already in use: bind
```
**Solution:** Stop other processes on port 8080

## Best Practices

1. **Always create test data via API** - Never assume data exists
2. **Use unique identifiers** - `TestDataGenerator` ensures uniqueness
3. **Store tokens in context** - Don't hardcode JWT tokens
4. **Clean assertions** - One concept per step
5. **Tag appropriately** - Use @smoke, @security, @negative for filtering

