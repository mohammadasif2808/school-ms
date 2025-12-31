# Global Exception Handler Implementation

## Overview

Implemented centralized global exception handling for identity-service using Spring's `@RestControllerAdvice`. All errors now return a standardized error response format across all endpoints.

---

## Components

### 1. ErrorResponse DTO

**Location:** `src/main/java/com/school/identity/dto/ErrorResponse.java`

**Responsibility:** Standard error response model used by all error handlers

**Fields:**
- `status` (Integer) — HTTP status code (400, 401, 403, 404, 500)
- `error` (String) — Machine-readable error code (VALIDATION_ERROR, UNAUTHORIZED, etc.)
- `message` (String) — Human-readable error message
- `details` (Map) — Field-level validation errors (only for validation errors)
- `timestamp` (LocalDateTime) — ISO-8601 timestamp when error occurred
- `method` (String) — HTTP method (GET, POST, etc.)
- `path` (String) — Request path that caused error

**Example:**
```json
{
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "details": {
    "username": "Username must be between 3 and 50 characters",
    "email": "Email must be valid"
  },
  "timestamp": "2026-01-01T10:30:00",
  "method": "POST",
  "path": "/api/v1/auth/signup"
}
```

### 2. GlobalExceptionHandler

**Location:** `src/main/java/com/school/identity/exception/GlobalExceptionHandler.java`

**Responsibility:** Centralized exception handling for all controllers

**Annotation:** `@RestControllerAdvice` — Applies to all `@RestController` classes

**Handled Exception Types:**

#### ValidationException (Custom)
- **Status:** 400 Bad Request
- **Error Code:** Custom code from exception (ROLE_EXISTS, USER_NOT_FOUND, etc.)
- **Trigger:** Service layer business rule violations
- **Response:** Status + error + message

#### MethodArgumentNotValidException (Spring)
- **Status:** 400 Bad Request
- **Error Code:** VALIDATION_ERROR
- **Trigger:** @Valid annotation failures on DTOs
- **Response:** Status + error + field-level details map

#### AuthenticationException (Spring Security)
- **Status:** 401 Unauthorized
- **Error Code:** UNAUTHORIZED
- **Trigger:** JWT validation failures, missing token
- **Response:** Status + error + message

#### AccessDeniedException (Spring Security)
- **Status:** 403 Forbidden
- **Error Code:** FORBIDDEN
- **Trigger:** Missing required permission (@PreAuthorize failures)
- **Response:** Status + error + message

#### NoHandlerFoundException (Spring)
- **Status:** 404 Not Found
- **Error Code:** NOT_FOUND
- **Trigger:** Requested endpoint doesn't exist
- **Response:** Status + error + message

#### Exception (Fallback)
- **Status:** 500 Internal Server Error
- **Error Code:** INTERNAL_SERVER_ERROR
- **Trigger:** Any unhandled exception
- **Response:** Status + error + generic message
- **Logging:** Full stack trace logged for debugging

---

## Error Response Format

All errors follow this consistent JSON structure:

```json
{
  "status": <HTTP_STATUS_CODE>,
  "error": "<ERROR_CODE>",
  "message": "<HUMAN_READABLE_MESSAGE>",
  "details": <OPTIONAL_FIELD_ERRORS>,
  "timestamp": "<ISO-8601_TIMESTAMP>",
  "method": "<HTTP_METHOD>",
  "path": "<REQUEST_PATH>"
}
```

### Fields Explained

| Field | Always Present | Purpose |
|-------|----------------|---------|
| status | ✅ Yes | HTTP status code for debugging |
| error | ✅ Yes | Machine code for client handling |
| message | ✅ Yes | User-friendly explanation |
| details | ❌ No | Only for validation errors with field details |
| timestamp | ✅ Yes | When error occurred (ISO-8601) |
| method | ⚠️ Sometimes | HTTP method (for request-based errors) |
| path | ⚠️ Sometimes | Request path (for request-based errors) |

---

## Exception Handling Flow

```
HTTP Request
    ↓
Controller Method
    ├─ DTO validation fails?
    │  └─ MethodArgumentNotValidException
    │
    ├─ Service throws ValidationException?
    │  └─ ValidationException (custom)
    │
    ├─ Spring Security auth fails?
    │  └─ AuthenticationException
    │
    ├─ Spring Security permission check fails?
    │  └─ AccessDeniedException
    │
    ├─ Endpoint doesn't exist?
    │  └─ NoHandlerFoundException
    │
    └─ Unexpected error?
       └─ Exception (catch-all)

    ↓
GlobalExceptionHandler (@RestControllerAdvice)
    ├─ Match exception type
    ├─ Create ErrorResponse DTO
    ├─ Log (warn/error)
    └─ Return ResponseEntity

    ↓
HTTP Response (JSON)
{
  "status": <CODE>,
  "error": "<ERROR_CODE>",
  "message": "...",
  "timestamp": "2026-01-01T10:30:00"
}
```

---

## Error Codes Reference

### 400 Bad Request

| Code | Trigger | Example |
|------|---------|---------|
| VALIDATION_ERROR | @Valid DTO validation fails | Missing required field |
| ROLE_EXISTS | Role name already exists | Duplicate role creation |
| PERMISSION_EXISTS | Permission code already exists | Duplicate permission code |
| USERNAME_EXISTS | Username already taken | Sign up with existing username |
| EMAIL_EXISTS | Email already taken | Sign up with existing email |
| PASSWORD_WEAK | Password doesn't meet requirements | Missing uppercase/digit |
| USER_NOT_FOUND | User doesn't exist | Via service business logic |
| ROLE_NOT_FOUND | Role doesn't exist | Via service business logic |
| PERMISSION_NOT_FOUND | Permission doesn't exist | Via service business logic |

### 401 Unauthorized

| Code | Trigger | Example |
|------|---------|---------|
| UNAUTHORIZED | JWT invalid/expired/missing | Missing Authorization header |
| INVALID_CREDENTIALS | Username/password incorrect | Wrong password |
| ACCOUNT_INACTIVE | User account not active | User status not ACTIVE |
| ACCOUNT_BLOCKED | User account blocked | User manually blocked |

### 403 Forbidden

| Code | Trigger | Example |
|------|---------|---------|
| FORBIDDEN | Missing required permission | @PreAuthorize fails |
| INSUFFICIENT_PERMISSIONS | Insufficient permissions | Multiple permissions check fails |

### 404 Not Found

| Code | Trigger | Example |
|------|---------|---------|
| NOT_FOUND | Endpoint doesn't exist | Invalid URL path |

### 500 Internal Server Error

| Code | Trigger | Example |
|------|---------|---------|
| INTERNAL_SERVER_ERROR | Unexpected exception | Database connection error |

---

## Request/Response Examples

### Example 1: Validation Error (Missing Field)

**Request:**
```json
POST /api/v1/auth/signup
{
  "username": "john_doe"
  // Missing email, password, first_name, last_name, phone
}
```

**Response (400 Bad Request):**
```json
{
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "details": {
    "email": "Email is required",
    "password": "Password is required",
    "first_name": "First name is required",
    "last_name": "Last name is required",
    "phone": "Phone is required"
  },
  "timestamp": "2026-01-01T10:30:00.123456",
  "method": "POST",
  "path": "/api/v1/auth/signup"
}
```

### Example 2: Custom Validation Error (Duplicate Role)

**Request:**
```json
POST /api/v1/admin/roles
{
  "name": "Teacher",
  "description": "Teacher role"
}
```

**Response (400 Bad Request) — Role "Teacher" already exists:**
```json
{
  "status": 400,
  "error": "ROLE_EXISTS",
  "message": "Role with name 'Teacher' already exists",
  "timestamp": "2026-01-01T10:31:00.123456",
  "method": "POST",
  "path": "/api/v1/admin/roles"
}
```

### Example 3: Authentication Error (Missing Token)

**Request:**
```
GET /api/v1/auth/me
(No Authorization header)
```

**Response (401 Unauthorized):**
```json
{
  "status": 401,
  "error": "UNAUTHORIZED",
  "message": "Authentication failed",
  "timestamp": "2026-01-01T10:32:00.123456",
  "method": "GET",
  "path": "/api/v1/auth/me"
}
```

### Example 4: Authorization Error (Missing Permission)

**Request:**
```
POST /api/v1/admin/roles
Authorization: Bearer <token>
```

**Response (403 Forbidden) — User lacks ROLE_MANAGE permission:**
```json
{
  "status": 403,
  "error": "FORBIDDEN",
  "message": "Access denied - insufficient permissions",
  "timestamp": "2026-01-01T10:33:00.123456",
  "method": "POST",
  "path": "/api/v1/admin/roles"
}
```

### Example 5: Endpoint Not Found

**Request:**
```
POST /api/v1/invalid/endpoint
```

**Response (404 Not Found):**
```json
{
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Requested endpoint not found",
  "timestamp": "2026-01-01T10:34:00.123456",
  "method": "POST",
  "path": "/api/v1/invalid/endpoint"
}
```

### Example 6: Unexpected Error

**Response (500 Internal Server Error):**
```json
{
  "status": 500,
  "error": "INTERNAL_SERVER_ERROR",
  "message": "An unexpected error occurred",
  "timestamp": "2026-01-01T10:35:00.123456",
  "method": "POST",
  "path": "/api/v1/auth/signin"
}
```

---

## Logging Strategy

**Log Levels by Exception Type:**

| Exception | Level | Details Logged |
|-----------|-------|----------------|
| ValidationException | WARN | Error code + message |
| MethodArgumentNotValidException | WARN | "Request validation failed" |
| AuthenticationException | WARN | Message |
| AccessDeniedException | WARN | Message |
| NoHandlerFoundException | WARN | HTTP method + URL |
| Exception (Fallback) | ERROR | Full stack trace |

**Example Log Output:**
```
2026-01-01 10:30:00.123 WARN  - Validation error: ROLE_EXISTS - Role with name 'Teacher' already exists
2026-01-01 10:31:00.456 WARN  - Request validation failed
2026-01-01 10:32:00.789 WARN  - Authentication error: Token expired
2026-01-01 10:33:00.012 WARN  - Access denied: Missing required permission
2026-01-01 10:34:00.345 ERROR - Unexpected error
java.lang.NullPointerException: ...
    at com.school.identity.service.UserService.getUser(UserService.java:45)
    ...
```

---

## Benefits

✅ **Consistency** — All errors return same format
✅ **Client-Friendly** — Error codes enable client-side handling
✅ **Human-Readable** — Clear messages for end users
✅ **Debugging** — Timestamps and paths help troubleshooting
✅ **Field-Level Details** — Validation errors show which fields failed
✅ **Logging** — Warnings for expected errors, errors for unexpected
✅ **No Code Changes** — Works with existing controllers
✅ **Spring Integration** — Uses @RestControllerAdvice (Spring standard)

---

## No Changes to Existing Code

✅ Controllers unchanged
✅ Services unchanged
✅ Business logic unchanged
✅ No new endpoints
✅ Backward compatible with existing error handling

---

## Configuration (Optional)

**In application.yml:**
```yaml
server:
  error:
    # Include error details in response
    include-message: always
    include-binding-errors: always
    include-stacktrace: on-param
    include-exception: false
```

---

## Integration with Controllers

Controllers no longer need to manually create error responses:

**Before (Manual Error Handling):**
```java
@PostMapping("/roles")
public ResponseEntity<?> createRole(@Valid @RequestBody CreateRoleRequest request) {
    try {
        RoleResponse response = adminService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (ValidationException e) {
        return ResponseEntity.badRequest()
            .body(createErrorResponse(e.getErrorCode(), e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(createErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
    }
}
```

**After (With Global Handler):**
```java
@PostMapping("/roles")
public ResponseEntity<?> createRole(@Valid @RequestBody CreateRoleRequest request) {
    RoleResponse response = adminService.createRole(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

Exceptions are automatically caught and formatted by `GlobalExceptionHandler`.

---

## Testing

### Unit Test Example

```java
@Test
public void testValidationErrorResponse() {
    // POST with missing required field
    mockMvc.perform(post("/api/v1/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.details").isMap());
}

@Test
public void testAuthenticationErrorResponse() {
    // GET without token
    mockMvc.perform(get("/api/v1/auth/me"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401))
        .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
}

@Test
public void testAccessDeniedErrorResponse() {
    // POST without required permission
    mockMvc.perform(post("/api/v1/admin/roles")
            .header("Authorization", "Bearer " + tokenWithoutPermission)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Role\"}"))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status").value(403))
        .andExpect(jsonPath("$.error").value("FORBIDDEN"));
}
```

---

## Status

✅ Global exception handler implemented
✅ Standardized error response format
✅ All exception types handled
✅ Logging configured
✅ No changes to existing code
✅ Zero compilation errors
✅ Ready for production

