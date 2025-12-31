# Global Exception Handler - Quick Reference

## What Was Implemented

### 1 Error Response DTO
- **ErrorResponse.java** — Standard error response for all endpoints

### 1 Global Exception Handler
- **GlobalExceptionHandler.java** — Centralized exception handling via @RestControllerAdvice

---

## Handled Exception Types

| Exception | HTTP Status | Error Code | Trigger |
|-----------|-------------|-----------|---------|
| ValidationException | 400 | ROLE_EXISTS, USER_NOT_FOUND, etc. | Service business logic |
| MethodArgumentNotValidException | 400 | VALIDATION_ERROR | @Valid DTO validation |
| AuthenticationException | 401 | UNAUTHORIZED | JWT invalid/expired/missing |
| AccessDeniedException | 403 | FORBIDDEN | Missing permission |
| NoHandlerFoundException | 404 | NOT_FOUND | Invalid endpoint |
| Exception (Fallback) | 500 | INTERNAL_SERVER_ERROR | Unexpected error |

---

## Standard Error Response

```json
{
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "details": {
    "field1": "error message",
    "field2": "error message"
  },
  "timestamp": "2026-01-01T10:30:00",
  "method": "POST",
  "path": "/api/v1/auth/signup"
}
```

### Fields

| Field | Always Present | Purpose |
|-------|---|---|
| status | ✅ | HTTP status code |
| error | ✅ | Machine-readable code |
| message | ✅ | Human-readable message |
| details | ❌ | Field-level validation errors only |
| timestamp | ✅ | ISO-8601 timestamp |
| method | ⚠️ | HTTP method |
| path | ⚠️ | Request path |

---

## Common Error Codes

### 400 Bad Request
- VALIDATION_ERROR — DTO field validation failed
- ROLE_EXISTS — Role name already exists
- PERMISSION_EXISTS — Permission code already exists
- PASSWORD_WEAK — Password doesn't meet requirements
- USER_NOT_FOUND — User not found

### 401 Unauthorized
- UNAUTHORIZED — Token missing/invalid/expired
- INVALID_CREDENTIALS — Wrong username or password
- ACCOUNT_INACTIVE — User account not active

### 403 Forbidden
- FORBIDDEN — Missing required permission

### 404 Not Found
- NOT_FOUND — Endpoint doesn't exist

### 500 Internal Server Error
- INTERNAL_SERVER_ERROR — Unexpected error

---

## Example Error Responses

### Validation Error (Missing Fields)
```json
{
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "details": {
    "username": "Username is required",
    "email": "Email must be valid"
  },
  "timestamp": "2026-01-01T10:30:00"
}
```

### Custom Validation Error (Duplicate)
```json
{
  "status": 400,
  "error": "ROLE_EXISTS",
  "message": "Role with name 'Teacher' already exists",
  "timestamp": "2026-01-01T10:31:00"
}
```

### Authentication Error (No Token)
```json
{
  "status": 401,
  "error": "UNAUTHORIZED",
  "message": "Authentication failed",
  "timestamp": "2026-01-01T10:32:00"
}
```

### Authorization Error (No Permission)
```json
{
  "status": 403,
  "error": "FORBIDDEN",
  "message": "Access denied - insufficient permissions",
  "timestamp": "2026-01-01T10:33:00"
}
```

---

## Logging

**By Exception Type:**
- ValidationException → WARN
- MethodArgumentNotValidException → WARN
- AuthenticationException → WARN
- AccessDeniedException → WARN
- NoHandlerFoundException → WARN
- Exception (fallback) → ERROR (with stack trace)

---

## Key Benefits

✅ **Consistency** — Same format everywhere
✅ **Client-Friendly** — Error codes for handling
✅ **Human-Readable** — Clear messages
✅ **Automatic** — Applied to all controllers
✅ **No Code Changes** — Existing controllers work as-is
✅ **Field Details** — Validation shows which fields failed
✅ **Debugging** — Timestamps and paths for troubleshooting

---

## How It Works

```
Exception thrown
    ↓
GlobalExceptionHandler catches it
    ↓
Matches exception type
    ↓
Creates ErrorResponse DTO
    ↓
Logs (warn/error)
    ↓
Returns ResponseEntity with ErrorResponse
    ↓
Client receives JSON with error details
```

---

## Verification

✅ ErrorResponse.java compiles
✅ GlobalExceptionHandler.java compiles
✅ Handles 6 exception types
✅ Standardizes all error responses
✅ No changes to existing controllers
✅ Ready for production

---

## Status

✅ Global exception handling complete
✅ Standard error response format
✅ All exception types covered
✅ Zero compilation errors
✅ Ready for testing and deployment

