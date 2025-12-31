# AuthenticationController - REST API Implementation

## Overview

The `AuthenticationController` implements the public REST API endpoints for user authentication as defined in the OpenAPI contract.

**Base Path:** `/api/v1/auth/**`

**Responsibility:** Thin controller layer that:
- Accepts HTTP requests
- Delegates business logic to services
- Maps entities to DTOs
- Returns proper HTTP status codes and response formats
- Handles exceptions and error responses

---

## Architecture

```
┌─────────────────────────────────────┐
│   HTTP Client (Frontend/Gateway)    │
└─────────────┬───────────────────────┘
              │ REST API calls
              ▼
┌─────────────────────────────────────┐
│  AuthenticationController           │ (THIS FILE)
│  - signUp()                         │
│  - signIn()                         │
│  - signOut()                        │
│  - getCurrentUser()                 │
└─────────────┬───────────────────────┘
              │ delegates to services
         ┌────┴───────┐
         │            │
         ▼            ▼
    ┌─────────┐  ┌─────────┐
    │ Auth    │  │ Jwt     │
    │Service  │  │Service  │
    └────┬────┘  └────┬────┘
         │            │
         └────┬───────┘
              │ calls repositories
              ▼
         ┌─────────┐
         │ Database│
         └─────────┘
```

---

## Endpoints

### 1. Sign Up (User Registration)

**Endpoint:** `POST /api/v1/auth/signup`

**Purpose:** Create new user account

**Request Body:** SignUpRequest
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecureP@ss123",
  "first_name": "John",
  "last_name": "Doe",
  "phone": "+1234567890"
}
```

**Success Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "john_doe",
  "email": "john@example.com",
  "first_name": "John",
  "last_name": "Doe",
  "phone": "+1234567890",
  "status": "ACTIVE",
  "created_at": "2026-01-01T12:00:00Z"
}
```

**Error Responses:**

| HTTP | Error Code | Scenario |
|------|-----------|----------|
| 400 | VALIDATION_ERROR | Missing/invalid required fields |
| 400 | PASSWORD_WEAK | Password doesn't meet complexity requirements |
| 409 | USERNAME_EXISTS | Username already registered |
| 409 | EMAIL_EXISTS | Email already registered |
| 500 | INTERNAL_SERVER_ERROR | Server error |

**Implementation Flow:**
```
1. @PostMapping("/signup")
2. @Valid SignUpRequest validates DTO
3. authenticationService.signUp(request)
   ├─ Validates fields
   ├─ Checks uniqueness
   ├─ Hashes password
   └─ Returns User entity
4. mapToSignUpResponse(user)
5. ResponseEntity.status(201).body(response)
```

---

### 2. Sign In (User Authentication)

**Endpoint:** `POST /api/v1/auth/signin`

**Purpose:** Authenticate user and issue JWT token

**Request Body:** SignInRequest
```json
{
  "username": "john_doe",
  "password": "SecureP@ss123"
}
```

**Success Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "john_doe",
    "email": "john@example.com",
    "first_name": "John",
    "last_name": "Doe",
    "avatar_url": null,
    "status": "ACTIVE"
  }
}
```

**Error Responses:**

| HTTP | Error Code | Scenario |
|------|-----------|----------|
| 400 | VALIDATION_ERROR | Missing username or password |
| 401 | INVALID_CREDENTIALS | User not found or password mismatch |
| 403 | ACCOUNT_INACTIVE | User account is inactive |
| 403 | ACCOUNT_BLOCKED | User account is blocked |
| 500 | INTERNAL_SERVER_ERROR | Server error |

**Implementation Flow:**
```
1. @PostMapping("/signin")
2. @Valid SignInRequest validates DTO
3. authenticationService.signIn(request)
   ├─ Validates fields
   ├─ Finds user by username/email
   ├─ Checks soft delete
   ├─ Validates status
   ├─ Verifies password
   └─ Returns User entity
4. jwtService.generateToken(user)
   ├─ Extracts permissions
   ├─ Creates claims
   ├─ Signs token
   └─ Returns JWT string
5. jwtService.extractPermissions(user) for response
6. mapToSignInResponse(user, token, permissions)
7. ResponseEntity.ok(response)
```

---

### 3. Sign Out (User Logout)

**Endpoint:** `POST /api/v1/auth/signout`

**Purpose:** Log out user and invalidate token

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...
```

**Request Body:** Empty or optional
```json
{}
```

**Success Response (200 OK):**
```json
{
  "message": "Successfully signed out"
}
```

**Error Responses:**

| HTTP | Error Code | Scenario |
|------|-----------|----------|
| 401 | UNAUTHORIZED | Missing or invalid token |
| 500 | INTERNAL_SERVER_ERROR | Server error |

**Implementation Flow:**
```
1. @PostMapping("/signout")
2. Extract Authorization header
3. jwtService.isTokenValid(authHeader)
4. createMessageResponse("Successfully signed out")
5. ResponseEntity.ok(response)
```

**Note:** Currently validates token syntax. Full logout (token blacklist) is a future enhancement.

---

### 4. Get Current Authenticated User

**Endpoint:** `GET /api/v1/auth/me`

**Purpose:** Retrieve current user's profile and permissions

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...
```

**Success Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "john_doe",
  "email": "john@example.com",
  "first_name": "John",
  "last_name": "Doe",
  "phone": "+1234567890",
  "avatar_url": null,
  "is_super_admin": false,
  "status": "ACTIVE",
  "role": "TEACHER",
  "permissions": ["STUDENT_VIEW", "ATTENDANCE_MARK"],
  "created_at": "2026-01-01T12:00:00Z"
}
```

**Error Responses:**

| HTTP | Error Code | Scenario |
|------|-----------|----------|
| 401 | UNAUTHORIZED | Token required |
| 401 | UNAUTHORIZED | Invalid or expired token |
| 403 | FORBIDDEN | User has been deleted |
| 404 | USER_NOT_FOUND | User not found in database |
| 500 | INTERNAL_SERVER_ERROR | Server error |

**Implementation Flow:**
```
1. @GetMapping("/me")
2. Check Authorization header present
3. jwtService.validateTokenAndGetUser(authHeader)
   ├─ Validates signature
   ├─ Checks expiration
   ├─ Looks up user in DB
   ├─ Checks not soft-deleted
   └─ Returns User entity
4. jwtService.extractPermissions(user)
5. jwtService.extractPrimaryRole(user)
6. mapToCurrentUserResponse(user, permissions, role)
7. ResponseEntity.ok(response)
```

---

## Exception Handling

### Exception Hierarchy

```
Exception
├─ ValidationException
│  └─ Error Codes:
│     ├─ VALIDATION_ERROR → 400
│     ├─ PASSWORD_WEAK → 400
│
├─ AuthenticationException
│  └─ Error Codes:
│     ├─ USERNAME_EXISTS → 409
│     ├─ EMAIL_EXISTS → 409
│     ├─ INVALID_CREDENTIALS → 401
│     ├─ ACCOUNT_INACTIVE → 403
│     ├─ ACCOUNT_BLOCKED → 403
│
├─ JwtException
│  └─ Error Codes:
│     ├─ TOKEN_EXPIRED → 401
│     ├─ TOKEN_INVALID → 401
│     ├─ USER_NOT_FOUND → 404
│     ├─ USER_DELETED → 403
│
└─ Generic Exception → 500
```

### Error Response Format

```json
{
  "error": "ERROR_CODE",
  "message": "Human-readable message"
}
```

---

## HTTP Status Codes

| Code | Meaning | When Used |
|------|---------|-----------|
| 200 | OK | signIn, signOut, getCurrentUser success |
| 201 | Created | signUp success |
| 400 | Bad Request | Validation errors, weak password |
| 401 | Unauthorized | Invalid credentials, invalid/expired token |
| 403 | Forbidden | Account inactive/blocked, user deleted |
| 404 | Not Found | User not found (after token validation) |
| 409 | Conflict | Duplicate username or email |
| 500 | Internal Server Error | Unexpected server errors |

---

## Dependency Injection

```java
@RestController
public class AuthenticationController {
    
    private final AuthenticationService authenticationService;  // Injected
    private final JwtService jwtService;                       // Injected
    
    // Constructor injection (required by AI_RULES.md)
    public AuthenticationController(
        AuthenticationService authenticationService,
        JwtService jwtService
    ) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }
}
```

**Key Points:**
- Constructor injection only
- No field injection (@Autowired)
- Both services required (no Optional)
- Immutable final fields

---

## Request Validation

### Validation Flow

```
HTTP Request
    ↓
@Valid annotation
    ↓
Jakarta Bean Validation
    ├─ NotBlank checks
    ├─ Size checks
    ├─ Email checks
    ├─ Pattern checks
    └─ Custom validators
    ↓
If invalid: 400 Bad Request
    ↓
If valid: Proceed to method
```

### DTO Validation Annotations

**SignUpRequest:**
- username: @NotBlank, @Size(3-50), @Pattern (alphanumeric + hyphens/underscores)
- email: @NotBlank, @Email
- password: @NotBlank, @Size(min=8)
- first_name: @NotBlank, @Size(1-100)
- last_name: @NotBlank, @Size(1-100)
- phone: @NotBlank

**SignInRequest:**
- username: @NotBlank
- password: @NotBlank

---

## DTO Mapping

### SignUpRequest → User → SignUpResponse

```
SignUpRequest (HTTP input)
    ↓
AuthenticationService.signUp()
    ├─ Creates User entity
    ├─ Sets all fields
    ├─ Hashes password
    └─ Persists to DB
    ↓
User entity (JPA)
    ↓
mapToSignUpResponse()
    ├─ Extracts fields
    ├─ Converts to DTO
    └─ Excludes password
    ↓
SignUpResponse (HTTP output)
```

### SignInRequest → User → SignInResponse

```
SignInRequest (HTTP input)
    ↓
AuthenticationService.signIn()
    ├─ Validates credentials
    ├─ Returns User entity
    ↓
JwtService.generateToken()
    ├─ Creates JWT token
    └─ Returns String
    ↓
mapToSignInResponse()
    ├─ Extracts user fields
    ├─ Adds token
    └─ Converts to DTO
    ↓
SignInResponse (HTTP output)
```

### Authorized Request → User → CurrentUserResponse

```
GET /api/v1/auth/me with Bearer token
    ↓
Extract Authorization header
    ↓
JwtService.validateTokenAndGetUser()
    ├─ Validates token
    ├─ Looks up user in DB
    └─ Returns User entity
    ↓
Extract permissions and role
    ├─ JwtService.extractPermissions()
    ├─ JwtService.extractPrimaryRole()
    ↓
mapToCurrentUserResponse()
    ├─ Builds response map
    ├─ Includes all user fields
    └─ Adds permissions and role
    ↓
CurrentUserResponse (HTTP output)
```

---

## Helper Methods

### mapToSignUpResponse()
Maps User entity to SignUpResponse DTO (excludes password)

### mapToSignInResponse()
Maps User entity + token to SignInResponse DTO

### mapToCurrentUserResponse()
Maps User entity + permissions/role to CurrentUserResponse map

### createErrorResponse()
Creates standard error response with code and message

### createMessageResponse()
Creates standard message response (for signOut)

---

## Code Quality

✅ **Thin Controller:** No business logic
✅ **Service Delegation:** All work delegated to services
✅ **Proper Status Codes:** Matches OpenAPI contract
✅ **DTO Validation:** @Valid on all requests
✅ **Exception Handling:** Specific catch blocks for each exception type
✅ **Constructor Injection:** No field injection
✅ **JavaDoc Comments:** All methods documented
✅ **Clean Code:** Helper methods extracted for mapping
✅ **No Cross-Concerns:** No logging, caching, or auth logic

---

## Testing Recommendations

### Unit Tests

```java
// Test signUp success
@Test
public void testSignUpSuccess() {
    SignUpRequest request = new SignUpRequest(...);
    ResponseEntity<?> response = controller.signUp(request);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
}

// Test signUp duplicate username
@Test
public void testSignUpDuplicateUsername() {
    SignUpRequest request = new SignUpRequest(...);
    // Setup: authService throws AuthenticationException(USERNAME_EXISTS)
    ResponseEntity<?> response = controller.signUp(request);
    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
}

// Test signIn success
@Test
public void testSignInSuccess() {
    SignInRequest request = new SignInRequest(...);
    ResponseEntity<?> response = controller.signIn(request);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody() instanceof SignInResponse);
}

// Test signIn invalid credentials
@Test
public void testSignInInvalidCredentials() {
    SignInRequest request = new SignInRequest(...);
    // Setup: authService throws AuthenticationException(INVALID_CREDENTIALS)
    ResponseEntity<?> response = controller.signIn(request);
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
}

// Test getCurrentUser
@Test
public void testGetCurrentUserSuccess() {
    String token = "Bearer eyJ...";
    ResponseEntity<?> response = controller.getCurrentUser(token);
    assertEquals(HttpStatus.OK, response.getStatusCode());
}

// Test getCurrentUser expired token
@Test
public void testGetCurrentUserExpiredToken() {
    String token = "Bearer eyJ...";
    // Setup: jwtService throws JwtException(TOKEN_EXPIRED)
    ResponseEntity<?> response = controller.getCurrentUser(token);
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
}
```

### Integration Tests

- Sign up → verify user created in database
- Sign up → sign in → verify token valid
- Sign in → /me → verify user data matches
- Token expired → /me → verify 401 response
- User deleted → /me with valid token → verify 403 response

---

## Integration with Other Components

### AuthenticationService (Existing)
- `signUp(SignUpRequest) → User`
- `signIn(SignInRequest) → User`

### JwtService (Existing)
- `generateToken(User) → String`
- `validateTokenAndGetUser(String) → User`
- `isTokenValid(String) → boolean`
- `extractPermissions(User) → List<String>`
- `extractPrimaryRole(User) → String`

### DTOs (Existing)
- SignUpRequest, SignUpResponse
- SignInRequest, SignInResponse
- JwtClaims (internal)

### Exceptions (Existing)
- ValidationException
- AuthenticationException
- JwtException

---

## Spring Annotations Used

| Annotation | Purpose |
|-----------|---------|
| @RestController | Mark class as REST controller |
| @RequestMapping("/api/v1/auth") | Base path for all endpoints |
| @PostMapping("/signup") | HTTP POST mapping |
| @PostMapping("/signin") | HTTP POST mapping |
| @PostMapping("/signout") | HTTP POST mapping |
| @GetMapping("/me") | HTTP GET mapping |
| @RequestBody | Map request body to DTO |
| @Valid | Trigger DTO validation |
| @RequestHeader | Extract Authorization header |

---

## Future Enhancements

- Global exception handler (cross-cutting concern)
- Refresh token endpoint
- Account lockout after failed attempts
- Rate limiting on signup/signin
- Audit logging
- CORS configuration
- OpenAPI documentation annotations

---

## Compliance

✅ **OpenAPI Contract:** Endpoints match contract exactly
✅ **README.md:** Public API endpoints implemented
✅ **AI_RULES.md:** Constructor injection, thin controller, service delegation
✅ **REST Best Practices:** Proper HTTP methods, status codes, response formats
✅ **Error Handling:** Specific error codes and messages
✅ **No Business Logic:** Only delegating to services
✅ **No Spring Security:** Just basic HTTP request handling

---

## What's NOT Implemented (Intentional)

- ❌ JWT Filter (for request validation)
- ❌ Spring Security configuration
- ❌ Global exception handler
- ❌ Token blacklist / logout mechanism
- ❌ Refresh token endpoint
- ❌ Rate limiting
- ❌ Audit logging
- ❌ OpenAPI annotations for Swagger/UI

These are separate concerns for future phases.

