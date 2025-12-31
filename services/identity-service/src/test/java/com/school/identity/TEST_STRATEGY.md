# Unit Testing Strategy for identity-service

## Overview

This document outlines the comprehensive unit testing strategy for the identity-service. 
Tests are designed to be high-quality, covering edge cases, security scenarios, and business logic.

---

## Testing Philosophy

### Principles
- **Quality over Quantity** — Each test case is meaningful and tests a specific behavior
- **GIVEN/WHEN/THEN** — Clear structure for readability and intention
- **Isolation** — Pure unit tests with mocked dependencies
- **Security Focus** — Edge cases that could lead to vulnerabilities
- **No Database** — All tests run without real database connection

### Test Naming Convention
```
methodName_givenCondition_shouldExpectedBehavior
```

Example:
```java
signIn_givenValidCredentials_shouldReturnUser()
signIn_givenWrongPassword_shouldThrowAuthenticationException()
```

---

## Test Categories

### 1. AuthenticationServiceTest
Tests for user registration and authentication logic.

**Scenarios:**
- Successful signup with valid data
- Signup with duplicate username
- Signup with duplicate email
- Signup with weak password (missing uppercase)
- Signup with weak password (missing lowercase)
- Signup with weak password (missing digit)
- Signup with weak password (missing special char)
- Signup with short password
- Signup with null/empty fields
- Successful signin with username
- Successful signin with email
- Signin with wrong password
- Signin with non-existent user
- Signin with inactive account
- Signin with blocked account
- Signin with deleted user (soft delete)

### 2. JwtTokenProviderTest
Tests for JWT token generation and validation.

**Scenarios:**
- Generate token with correct claims
- Token contains all required fields (userId, username, role, permissions, tenantId, iat, exp)
- Validate valid token
- Detect expired token
- Detect malformed token
- Detect invalid signature
- Extract userId from token
- Extract username from token
- Calculate time until expiry
- Handle "Bearer " prefix correctly

### 3. JwtServiceTest
Tests for high-level JWT operations.

**Scenarios:**
- Generate token for user with roles
- Generate token for user without roles
- Validate token and fetch user
- Validate token for deleted user
- Validate token for non-existent user
- Extract permissions from user roles
- Check if token is expiring soon

### 4. PermissionEvaluatorTest
Tests for RBAC permission evaluation.

**Scenarios:**
- User with required permission - allowed
- User without required permission - denied
- Super admin bypasses all permission checks
- hasAnyPermission with one matching permission
- hasAnyPermission with no matching permissions
- hasAllPermissions with all matching
- hasAllPermissions with one missing
- Null authentication - denied
- Unauthenticated user - denied
- Empty permissions list - denied (unless super admin)

### 5. PasswordResetServiceTest
Tests for forgot/reset password flow.

**Scenarios:**
- Request reset for existing user
- Request reset for non-existing user (no info leak)
- Request reset for deleted user (silent fail)
- Reset with valid token
- Reset with expired token
- Reset with already used token
- Reset with invalid token
- Reset with weak password
- Token generation is cryptographically random
- Token marked as used after reset

### 6. AdminServiceTest
Tests for role and permission management.

**Scenarios:**
- Create role successfully
- Create role with duplicate name
- Create permission successfully
- Create permission with duplicate code
- Assign permissions to role
- Assign permissions to non-existent role
- Assign non-existent permissions to role
- Assign roles to user
- Assign roles to non-existent user
- Assign roles to deleted user
- Get all roles
- Get role by ID
- Get all permissions
- Get permissions by module

### 7. GlobalExceptionHandlerTest
Tests for exception handling and error response format.

**Scenarios:**
- ValidationException returns 400
- AuthenticationException returns 401
- AccessDeniedException returns 403
- NoHandlerFoundException returns 404
- Generic Exception returns 500
- MethodArgumentNotValidException includes field errors
- Error response contains required fields (status, error, message)
- No stack trace in error response
- Proper error codes for different exceptions

### 8. AuthenticationControllerTest
Tests for REST endpoint behavior (MockMvc).

**Scenarios:**
- Signup endpoint with valid data
- Signup endpoint with validation errors
- Signin endpoint with valid credentials
- Signin endpoint with invalid credentials
- Signout endpoint with valid token
- Get current user with valid token
- Get current user without token
- Forgot password endpoint
- Reset password endpoint

### 9. AdminControllerTest
Tests for admin REST endpoints (MockMvc).

**Scenarios:**
- Create role with ROLE_MANAGE permission
- Create role without permission (403)
- Create role as super admin
- List roles with ROLE_VIEW permission
- Create permission with PERMISSION_MANAGE
- Assign permissions to role
- Assign roles to user

### 10. SecurityFilterTest
Tests for JWT authentication filter.

**Scenarios:**
- Request with valid token - authenticated
- Request without token - continues (for public endpoints)
- Request with invalid token - not authenticated
- Request with expired token - not authenticated
- Request with "Bearer " prefix handled correctly
- Request with malformed Authorization header

---

## Test Configuration

### Dependencies Used
- JUnit 5 (Jupiter)
- Mockito
- Spring Boot Test
- Spring Security Test
- MockMvc

### Annotations Used
- `@ExtendWith(MockitoExtension.class)` — Pure unit tests
- `@WebMvcTest` — Controller tests with Spring context
- `@Mock` — Mock dependencies
- `@InjectMocks` — Inject mocks into SUT
- `@MockBean` — Mock Spring beans in context

---

## Directory Structure

```
src/test/java/com/school/identity/
├── service/
│   ├── AuthenticationServiceTest.java
│   ├── JwtServiceTest.java
│   ├── PasswordResetServiceTest.java
│   ├── AdminServiceTest.java
│   └── EmailServiceTest.java
├── security/
│   ├── JwtTokenProviderTest.java
│   ├── PermissionEvaluatorTest.java
│   └── JwtAuthenticationFilterTest.java
├── controller/
│   ├── AuthenticationControllerTest.java
│   └── AdminControllerTest.java
├── exception/
│   └── GlobalExceptionHandlerTest.java
└── testutil/
    └── TestDataFactory.java
```

---

## Test Data Factory

Centralized test data creation for consistency:

```java
public class TestDataFactory {
    public static User createUser()
    public static User createSuperAdmin()
    public static Role createRole()
    public static Permission createPermission()
    public static SignUpRequest createValidSignUpRequest()
    public static SignInRequest createValidSignInRequest()
    // etc.
}
```

---

## Coverage Goals

| Component | Target Coverage |
|-----------|-----------------|
| AuthenticationService | 100% |
| JwtTokenProvider | 100% |
| JwtService | 95% |
| PermissionEvaluator | 100% |
| PasswordResetService | 100% |
| AdminService | 95% |
| GlobalExceptionHandler | 100% |
| Controllers | 90% |

---

## Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AuthenticationServiceTest

# Run with coverage report
mvn test jacoco:report
```

---

## Status

✅ Test strategy defined
✅ Test categories identified
✅ Edge cases documented
✅ Security scenarios covered
✅ Directory structure planned
✅ Ready for implementation

