# JWT Token Provider Implementation

## Overview

The JWT implementation provides stateless, distributed authentication across all microservices in the School Management System.

JWT tokens are issued by identity-service and trusted by all other services without requiring a backend lookup.

---

## Architecture

```
┌─────────────────────────────────┐
│    AuthenticationService        │
│  (Sign In Endpoint)             │
└────────────┬────────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│       JwtService (Facade)       │
│  - generateToken()              │
│  - validateToken()              │
│  - validateTokenAndGetUser()    │
└────────────┬────────────────────┘
             │
      ┌──────┴─────────┐
      ▼                ▼
┌──────────────┐  ┌──────────────────┐
│JwtTokenPrvdr │  │JwtClaimsBuilder  │
│- generate()  │  │- extractPerms()  │
│- validate()  │  │- extractRole()   │
│- extract()   │  │- hasPermission() │
└──────────────┘  └──────────────────┘
      │                │
      ▼                ▼
┌─────────────────────────────────┐
│   User Entity + Roles           │
│   (With Permissions)            │
└─────────────────────────────────┘
```

---

## Core Components

### 1. JwtTokenProvider (Security-Level Utility)

**Location:** `src/main/java/com/school/identity/security/JwtTokenProvider.java`

**Responsibility:** Low-level JWT token operations

**Public Methods:**

#### `generateToken(User user, List<String> permissions, String tenantId) -> String`
- Generates JWT token with all required claims
- Uses HMAC-SHA512 signing algorithm
- Claims include: userId, username, role, permissions, tenantId, iat, exp
- Expiration: Configurable (default: 24 hours)

**Example:**
```java
List<String> permissions = List.of("STUDENT_VIEW", "ATTENDANCE_MARK");
String token = jwtTokenProvider.generateToken(user, permissions, "school-001");
// Returns: "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9..."
```

#### `validateAndExtractClaims(String token) -> JwtClaims`
- Validates token signature using secret key
- Checks expiration
- Extracts all claims into JwtClaims DTO
- Throws JwtException if invalid or expired

**Error Cases:**
- `TOKEN_INVALID` — Malformed, signature mismatch, wrong format
- `TOKEN_EXPIRED` — Token past expiration time
- `TOKEN_INVALID` — Missing required claims

**Example:**
```java
try {
    JwtClaims claims = jwtTokenProvider.validateAndExtractClaims(token);
    System.out.println(claims.getUserId()); // UUID
    System.out.println(claims.getPermissions()); // List<String>
} catch (JwtException e) {
    // Handle token error
    System.err.println(e.getErrorCode()); // TOKEN_EXPIRED, TOKEN_INVALID
}
```

#### `isTokenValid(String token) -> boolean`
- Non-throwing version of validation
- Returns true/false without exceptions
- Useful for quick checks before detailed validation

**Example:**
```java
if (jwtTokenProvider.isTokenValid(token)) {
    // Safe to proceed
}
```

#### `extractUserId(String token) -> UUID`
- Extracts userId claim without full validation
- Use sparingly; prefer validateAndExtractClaims()

#### `extractUsername(String token) -> String`
- Extracts username claim without full validation
- Use sparingly; prefer validateAndExtractClaims()

#### `getTimeUntilExpiry(String token) -> long`
- Returns remaining seconds until token expiry
- Useful for refresh token logic
- Returns 0 if already expired

---

### 2. JwtClaimsBuilder (Business Logic Helper)

**Location:** `src/main/java/com/school/identity/security/JwtClaimsBuilder.java`

**Responsibility:** Extract business data (roles, permissions) from User entity

**Public Methods:**

#### `extractPermissionsFromRoles(User user) -> List<String>`
- Gets all distinct permission codes from user's roles
- Flattens role → permission mapping
- Returns empty list if no roles

**Example:**
```java
// User has 2 roles: TEACHER, ADMIN
// TEACHER role → [STUDENT_VIEW, ATTENDANCE_MARK]
// ADMIN role → [STUDENT_VIEW, STUDENT_EDIT, ROLE_MANAGE]
// Result: [STUDENT_VIEW, ATTENDANCE_MARK, STUDENT_EDIT, ROLE_MANAGE]

List<String> perms = claimsBuilder.extractPermissionsFromRoles(user);
```

#### `extractPrimaryRoleName(User user) -> String`
- Returns first role name (uppercase) or "USER" if none
- Used in JWT token "role" claim

#### `getAllRoleNames(User user) -> String`
- Returns comma-separated role names
- Useful for logging/audit

#### `hasPermission(User user, String permissionCode) -> boolean`
- Check if user has specific permission
- Traverses role→permission mapping
- Returns false if no roles

#### `isSuperAdmin(User user) -> boolean`
- Check if user.is_super_admin == true
- Super admins bypass permission checks

---

### 3. JwtService (Facade Service)

**Location:** `src/main/java/com/school/identity/service/JwtService.java`

**Responsibility:** High-level JWT operations for business logic

**Public Methods:**

#### `generateToken(User user) -> String`
- Generates token for authenticated user
- Automatically extracts permissions from roles
- Automatically uses configured tenantId
- Recommended for use in controllers

**Example:**
```java
// In SignIn endpoint:
User user = authenticationService.signIn(signInRequest);
String token = jwtService.generateToken(user);
```

#### `validateToken(String token) -> JwtClaims`
- Validates and extracts claims
- Throws JwtException on any error

#### `isTokenValid(String token) -> boolean`
- Non-throwing validation check

#### `validateTokenAndGetUser(String token) -> User`
- Validates token
- Fetches user from database
- Checks if user still exists and not deleted
- Recommended for use in filters/guards

**Error Cases:**
- `USER_NOT_FOUND` — User ID not in database
- `USER_DELETED` — User has is_deleted = true

#### `extractUserId(String token) -> UUID`
- Get user ID from token

#### `extractUsername(String token) -> String`
- Get username from token

#### `getTimeUntilExpiry(String token) -> long`
- Get remaining seconds until expiry

#### `isTokenExpiringSoon(String token, long thresholdSeconds) -> boolean`
- Check if token expires within threshold
- Useful for refresh token decision logic
- Returns true if invalid token (conservative)

#### `getTokenExpirationMs() -> Long`
- Get token expiration duration in milliseconds

#### `hasPermission(User user, String permissionCode) -> boolean`
- Check user permission

#### `isSuperAdmin(User user) -> boolean`
- Check if super admin

#### `extractPrimaryRole(User user) -> String`
- Get user's primary role

#### `extractPermissions(User user) -> List<String>`
- Get all user permissions

#### `getAllRoleNames(User user) -> String`
- Get comma-separated role names

---

### 4. JwtClaims DTO

**Location:** `src/main/java/com/school/identity/dto/JwtClaims.java`

**Purpose:** Container for extracted JWT claims

**Fields:**
```java
private UUID userId;              // User identifier
private String username;          // Username
private String role;              // Primary role (ADMIN, TEACHER, etc.)
private List<String> permissions; // Fine-grained permissions
private String tenantId;          // School/tenant identifier
private long iat;                 // Issued-at (epoch ms)
private long exp;                 // Expiration (epoch ms)
```

---

### 5. JwtException

**Location:** `src/main/java/com/school/identity/exception/JwtException.java`

**Error Codes:**
- `JWT_GENERATION_ERROR` — Failed to create token
- `TOKEN_EXPIRED` — Token past expiration
- `TOKEN_INVALID` — Invalid signature, malformed, wrong format
- `USER_NOT_FOUND` — User ID not in database
- `USER_DELETED` — User has been soft-deleted
- `TOKEN_VALIDATION_ERROR` — General validation failure

---

### 6. JwtProperties (Configuration)

**Location:** `src/main/java/com/school/identity/config/JwtProperties.java`

**Binds to:** `application.yml` under `jwt.*`

**Configuration Properties:**
```yaml
jwt:
  secret: ${JWT_SECRET:your-secret-key-change-in-production}
  expiration: ${JWT_EXPIRATION:86400000}           # 24 hours in ms
  refresh-expiration: ${JWT_REFRESH_EXPIRATION:604800000} # 7 days in ms
```

**Environment Variables:**
- `JWT_SECRET` — HMAC secret key (min 32 chars recommended)
- `JWT_EXPIRATION` — Token lifetime in milliseconds
- `JWT_REFRESH_EXPIRATION` — Refresh token lifetime in milliseconds

---

## JWT Token Structure

### Claims

```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "username": "john_doe",
  "role": "TEACHER",
  "permissions": ["STUDENT_VIEW", "ATTENDANCE_MARK"],
  "tenantId": "school-001",
  "iat": 1704067200,
  "exp": 1704153600
}
```

### Headers

```json
{
  "alg": "HS512",
  "typ": "JWT"
}
```

### Algorithm

- **Signature Algorithm:** HMAC-SHA512 (HS512)
- **Secret Key:** From `JWT_SECRET` configuration
- **Key Length:** Minimum 512 bits (64 bytes) recommended

---

## Token Generation Flow

```
1. User submits credentials (sign in)
   └─→ SignInRequest { username, password }

2. AuthenticationService validates credentials
   └─→ Returns User entity (if valid)

3. JwtService.generateToken(user) called
   ├─→ JwtClaimsBuilder extracts permissions from roles
   └─→ JwtTokenProvider creates token

4. Token contains claims:
   ├─→ userId (from user.id)
   ├─→ username (from user.username)
   ├─→ role (from user.roles)
   ├─→ permissions (from role.permissions)
   ├─→ tenantId (from config)
   ├─→ iat (issued-at)
   └─→ exp (expiration)

5. Token returned in SignInResponse
   └─→ { accessToken: "eyJ...", user: {...} }
```

---

## Token Validation Flow

```
1. Client sends request with Authorization header
   └─→ Authorization: Bearer eyJ...

2. Filter/Guard extracts token
   └─→ Removes "Bearer " prefix
   └─→ Passes token string

3. JwtService.validateTokenAndGetUser(token) called
   ├─→ JwtTokenProvider validates signature
   ├─→ JwtTokenProvider checks expiration
   ├─→ JwtTokenProvider extracts claims
   ├─→ UserRepository looks up user by userId
   └─→ Returns User entity (if all valid)

4. If token invalid
   └─→ JwtException thrown with error code
       ├─→ TOKEN_EXPIRED
       ├─→ TOKEN_INVALID
       ├─→ USER_NOT_FOUND
       └─→ USER_DELETED

5. If valid, user entity used for authorization checks
   └─→ Filter/Guard checks permissions
   └─→ Proceeds to controller or denies access
```

---

## Security Properties

### Secret Key Management
- Secret key stored in `JWT_SECRET` environment variable
- Never hardcoded in source
- Minimum 32 characters recommended (256 bits)
- Generated securely during deployment

### Token Signing
- HMAC-SHA512 algorithm (industry standard)
- Cryptographically secure signing
- Key is secret and known only by identity-service

### Token Validation
- Signature verified on every use
- No trust without valid signature
- Expiration checked on every use
- Cannot be modified without invalidating signature

### No Revocation Needed
- Tokens are stateless (no database lookup on use)
- Cannot be revoked while valid
- Only expiration stops token from working
- Future: Token blacklist for logout (separate implementation)

### Best Practices Applied
- ✅ Token expiration (24 hours default)
- ✅ Secure signing algorithm (HS512)
- ✅ All required claims present
- ✅ Constant-time comparison (JJWT library)
- ✅ No sensitive data in token (only codes, IDs)
- ✅ TenantId included for multi-tenant awareness

---

## Usage Examples

### Example 1: Generate Token After Sign In

```java
@Service
public class AuthenticationService {
    private final JwtService jwtService;
    
    public SignInResponse signIn(SignInRequest request) {
        // Validate credentials and get user
        User user = authenticateUser(request);
        
        // Generate token
        String token = jwtService.generateToken(user);
        
        // Build response
        SignInResponse response = new SignInResponse();
        response.setAccessToken(token);
        response.setUser(mapToUserInfo(user));
        return response;
    }
}
```

### Example 2: Validate Token in Filter

```java
// In security filter (next phase)
String token = request.getHeader("Authorization");
try {
    User user = jwtService.validateTokenAndGetUser(token);
    request.setAttribute("user", user);
    filterChain.doFilter(request, response);
} catch (JwtException e) {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.getWriter().write(e.getErrorCode());
}
```

### Example 3: Extract Claims Without User Lookup

```java
String token = extractFromRequest(request);
JwtClaims claims = jwtService.validateToken(token);
// Use claims directly without DB lookup
System.out.println(claims.getUserId());
System.out.println(claims.getPermissions());
```

### Example 4: Check Token Expiry Before Refresh

```java
String token = getTokenFromRequest(request);
if (jwtService.isTokenExpiringSoon(token, 300)) { // 5 min threshold
    // Refresh token
    newToken = jwtService.generateToken(user);
}
```

---

## Configuration Examples

### Development (Local)

```yaml
jwt:
  secret: dev-secret-key-min-32-chars-long-12345678
  expiration: 86400000        # 24 hours
  refresh-expiration: 604800000 # 7 days
```

### Production (Environment Variables)

```bash
export JWT_SECRET="$(openssl rand -hex 32)"  # 64 char hex = 256 bits
export JWT_EXPIRATION=86400000
export JWT_REFRESH_EXPIRATION=604800000
```

---

## Error Handling Reference

| Error Code | Cause | HTTP Status | Action |
|-----------|-------|-------------|--------|
| JWT_GENERATION_ERROR | Token creation failed | 500 | Log error, retry |
| TOKEN_EXPIRED | Token past expiration | 401 | Request new token |
| TOKEN_INVALID | Bad signature/format | 401 | Reject request |
| USER_NOT_FOUND | User not in DB | 404 | Log anomaly |
| USER_DELETED | User soft-deleted | 403 | Deny access |

---

## Testing Recommendations

### Unit Tests
- Token generation with various user roles
- Token validation with valid/expired/invalid tokens
- Claims extraction
- Permission lookup
- Edge cases (no roles, empty permissions)

### Integration Tests
- End-to-end sign-in → token generation
- Token validation with actual database
- User deletion → token rejection
- Concurrent token operations

---

## Future Enhancements

- Refresh token implementation
- Token blacklist for logout
- Multi-signing keys for key rotation
- Custom claims for tenant-specific data
- Token revocation checking
- Rate limiting on token generation
- Token audit logging

---

## Compliance

✅ Follows identity-service README.md (JWT contract)
✅ Follows OpenAPI contract (claims structure)
✅ Stateless (no backend state required)
✅ Trusted by all services
✅ MySQL compatible (no DB dependency for validation)
✅ Constructor injection (no field injection)
✅ Full JavaDoc documentation
✅ Custom exception types

