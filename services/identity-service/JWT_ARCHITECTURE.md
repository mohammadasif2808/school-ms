# JWT Token Provider - Architecture & Integration Guide

## Layer Integration

```
┌─────────────────────────────────────────────────────────┐
│           REST Controllers (Future Phase)               │
│  - POST /api/v1/auth/signin                            │
│  - GET /api/v1/auth/me                                 │
│  - POST /api/v1/auth/signout                           │
└────────────┬────────────────────────────────────────────┘
             │ (injects)
             ▼
┌─────────────────────────────────────────────────────────┐
│         Business Logic Services (Current)              │
│  ├─ AuthenticationService                              │
│  │  └─ signIn() → User                                 │
│  └─ JwtService (NEW)                                   │
│     ├─ generateToken(user) → String (JWT)              │
│     ├─ validateToken(token) → JwtClaims                │
│     └─ validateTokenAndGetUser(token) → User           │
└────────────┬────────────────────────────────────────────┘
             │ (injects)
             ▼
┌─────────────────────────────────────────────────────────┐
│         Security Utilities (NEW)                        │
│  ├─ JwtTokenProvider                                   │
│  │  ├─ generateToken()                                 │
│  │  ├─ validateAndExtractClaims()                      │
│  │  ├─ isTokenValid()                                  │
│  │  └─ extractUserId()/extractUsername()               │
│  └─ JwtClaimsBuilder                                   │
│     ├─ extractPermissionsFromRoles()                   │
│     ├─ extractPrimaryRoleName()                        │
│     ├─ hasPermission()                                 │
│     └─ isSuperAdmin()                                  │
└────────────┬────────────────────────────────────────────┘
             │ (uses)
             ▼
┌─────────────────────────────────────────────────────────┐
│          Domain & Data Access (Existing)               │
│  ├─ User (Entity)                                      │
│  ├─ Role (Entity)                                      │
│  ├─ Permission (Entity)                                │
│  └─ UserRepository                                     │
└──────────────────────────────────────────────────────────┘
```

---

## Sign In → Token Generation Flow

```
Client
  │
  └─→ POST /api/v1/auth/signin
       └─→ { username: "john_doe", password: "..." }
           
           ▼
    ┌─────────────────────────────┐
    │  AuthenticationController   │ (Future)
    └──────────┬──────────────────┘
               │ calls
               ▼
    ┌─────────────────────────────┐
    │ AuthenticationService       │
    │ .signIn(request)            │ → User entity
    └──────────┬──────────────────┘
               │ calls
               ▼
    ┌─────────────────────────────┐
    │   JwtService               │
    │   .generateToken(user)      │
    └──────────┬──────────────────┘
               │
         ┌─────┴──────┐
         │            │
         ▼            ▼
    ┌─────────┐  ┌──────────────────┐
    │JwtToken │  │JwtClaimsBuilder  │
    │Provider │  │                  │
    │         │  │extractPerms...() │
    │generate │  │extractRole()     │
    │Token()  │  │isSuperAdmin()    │
    └────┬────┘  └────────┬─────────┘
         │                │
         │ (uses)         │ (extracts)
         │                │
         ▼                ▼
    ┌─────────────────────────────┐
    │  User.getRoles()            │
    │  .flatMap(role.getPerms())  │
    └─────────────────────────────┘

    Result: JWT Token
    {
      "userId": "550e8400-...",
      "username": "john_doe",
      "role": "TEACHER",
      "permissions": ["STUDENT_VIEW", "ATTENDANCE_MARK"],
      "tenantId": "school-001",
      "iat": 1704067200,
      "exp": 1704153600
    }
    
    Encoded: eyJ0eXAiOiJKV1QiLCJhbGc...
    
           ▼
    ┌─────────────────────────────┐
    │  SignInResponse             │
    │  {                          │
    │    accessToken: "eyJ...",   │
    │    user: {...}              │
    │  }                          │
    └─────────────────────────────┘
           │
           └─→ Client
```

---

## Token Validation Flow

```
Client sends request
  │
  ├─ GET /api/v1/auth/me
  └─ Authorization: Bearer eyJ...
  
     ▼
  ┌────────────────────────────┐
  │  JWT Filter/Guard          │ (Future)
  │  (extracts token)          │
  └────────┬───────────────────┘
           │
           ▼
  ┌────────────────────────────┐
  │   JwtService               │
  │   .validateTokenAndGetUser()│
  └────────┬───────────────────┘
           │
     ┌─────┴──────────┐
     │                │
     ▼                ▼
  ┌────────────┐  ┌──────────────────┐
  │JwtToken    │  │ UserRepository   │
  │Provider    │  │                  │
  │.validate..│  │findById(userId)  │
  │ExtractCl()│  │                  │
  └─────┬──────┘  └────────┬─────────┘
        │                  │
        └─→ Signature OK   └─→ User found?
        └─→ Not expired       (not deleted?)
        └─→ All claims OK
        
        If ALL valid:
        ▼
  ┌────────────────────────────┐
  │  User entity               │
  │  (ready for use)           │
  └────────────────────────────┘
  
        If ANY fail:
        ▼
  ┌────────────────────────────┐
  │  JwtException              │
  │  - TOKEN_EXPIRED           │
  │  - TOKEN_INVALID           │
  │  - USER_NOT_FOUND          │
  │  - USER_DELETED            │
  └────────────────────────────┘
           │
           └─→ Reject request
               Send 401/403 response
```

---

## Claim Extraction & Permission Mapping

```
User Entity
├─ id: 550e8400-...
├─ username: john_doe
├─ roles: Set<Role>
│  ├─ Role: TEACHER
│  │  └─ permissions: Set<Permission>
│  │     ├─ Permission.code: STUDENT_VIEW
│  │     ├─ Permission.code: ATTENDANCE_MARK
│  │     └─ Permission.module: STUDENT
│  └─ Role: STAFF_ADMIN
│     └─ permissions: Set<Permission>
│        ├─ Permission.code: STUDENT_EDIT
│        ├─ Permission.code: STAFF_VIEW
│        └─ Permission.module: ADMIN
└─ is_super_admin: false

         ▼ (JwtClaimsBuilder processing)

Claims Extracted:
├─ userId: "550e8400-..."
├─ username: "john_doe"
├─ role: "TEACHER"
│  (primary = first role, uppercase)
├─ permissions: ["STUDENT_VIEW", "ATTENDANCE_MARK", "STUDENT_EDIT", "STAFF_VIEW"]
│  (distinct, flattened from all roles)
└─ tenantId: "school-001"

         ▼ (Token generation)

JWT Claims:
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "username": "john_doe",
  "role": "TEACHER",
  "permissions": [
    "STUDENT_VIEW",
    "ATTENDANCE_MARK",
    "STUDENT_EDIT",
    "STAFF_VIEW"
  ],
  "tenantId": "school-001",
  "iat": 1704067200,
  "exp": 1704153600
}
```

---

## Dependency Injection Architecture

```
@Component
public class JwtTokenProvider {
    private final JwtProperties jwtProperties;  ← Injected
    private final SecretKey secretKey;         ← Built from properties
    
    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(...);
    }
}

@Component
public class JwtClaimsBuilder {
    // No dependencies (works with passed-in User entity)
}

@Service
public class JwtService {
    private final JwtTokenProvider tokenProvider;    ← Injected
    private final JwtClaimsBuilder claimsBuilder;    ← Injected
    private final UserRepository userRepository;     ← Injected
    
    @Value("${service.tenant-id:default}")
    private String tenantId;                         ← From config
    
    public JwtService(
        JwtTokenProvider tokenProvider,
        JwtClaimsBuilder claimsBuilder,
        UserRepository userRepository
    ) {
        // Constructor injection only
        // No @Autowired fields
    }
}

In AuthenticationService or Controller:
    @Service
    public class AuthenticationService {
        private final JwtService jwtService;     ← Injected
        
        public AuthenticationService(JwtService jwtService) {
            this.jwtService = jwtService;
        }
        
        public void signIn(...) {
            User user = authenticate(...);
            String token = jwtService.generateToken(user);  ← Use
        }
    }
```

---

## Configuration Binding

```
application.yml
├─ jwt:
│  ├─ secret: ${JWT_SECRET:default}
│  ├─ expiration: ${JWT_EXPIRATION:86400000}
│  └─ refresh-expiration: ${JWT_REFRESH_EXPIRATION:604800000}
└─ service:
   └─ tenant-id: ${TENANT_ID:default}

         ▼ (Spring binds to)

@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;              ← jwt.secret
    private Long expiration;            ← jwt.expiration
    private Long refreshExpiration;     ← jwt.refresh-expiration
}

@Service
public class JwtService {
    @Value("${service.tenant-id:default}")
    private String tenantId;            ← service.tenant-id
}

Environment variables (production):
export JWT_SECRET="..."
export JWT_EXPIRATION="86400000"
export JWT_REFRESH_EXPIRATION="604800000"
export TENANT_ID="school-001"
```

---

## Token Lifecycle

```
1. TOKEN GENERATION
   ├─ User signs in
   ├─ Credentials validated
   ├─ JwtService.generateToken(user) called
   ├─ Token created with:
   │  ├─ Claims extracted from User/Roles
   │  ├─ iat = current time
   │  ├─ exp = iat + 24 hours
   │  └─ Signed with HMAC-SHA512
   └─ Token issued to client
   
   Status: VALID

2. TOKEN IN USE
   ├─ Client includes in Authorization header
   ├─ Each request: Token validated
   ├─ Signature verified
   ├─ Expiration checked
   ├─ User still exists (DB check)
   └─ Request proceeds
   
   Status: VALID

3. TOKEN NEAR EXPIRY
   ├─ Time remaining < threshold
   ├─ Application calls JwtService.isTokenExpiringSoon()
   ├─ Refresh logic triggered
   ├─ New token generated
   └─ Client gets new token
   
   Status: VALID (but expiring)

4. TOKEN EXPIRED
   ├─ Expiration time < current time
   ├─ JwtService.validateToken() fails
   ├─ JwtException thrown (TOKEN_EXPIRED)
   ├─ Request rejected (401 Unauthorized)
   └─ Client must re-authenticate
   
   Status: INVALID

5. TOKEN AFTER USER DELETION
   ├─ Token signature still valid
   ├─ But user.is_deleted = true
   ├─ JwtService.validateTokenAndGetUser() fails
   ├─ JwtException thrown (USER_DELETED)
   ├─ Request rejected (403 Forbidden)
   └─ User cannot use token anymore
   
   Status: INVALID (functionally)
```

---

## Method Call Sequences

### Sequence 1: Sign In and Get Token

```
AuthenticationController.signIn(SignInRequest)
│
├─→ AuthenticationService.signIn(request)
│   ├─→ Validate password
│   └─→ Return User
│
├─→ JwtService.generateToken(user)
│   ├─→ JwtClaimsBuilder.extractPermissionsFromRoles(user)
│   │   └─→ Return List<String> permissions
│   │
│   └─→ JwtTokenProvider.generateToken(user, permissions, tenantId)
│       └─→ Build claims Map
│       └─→ Set iat, exp
│       └─→ Sign with HMAC-SHA512
│       └─→ Return JWT String
│
└─→ SignInResponse { accessToken, user }
```

### Sequence 2: Validate Token on Request

```
Filter.doFilter(request, response, chain)
│
├─→ Extract token from Authorization header
│
├─→ JwtService.validateTokenAndGetUser(token)
│   ├─→ JwtTokenProvider.validateAndExtractClaims(token)
│   │   ├─→ Parse token
│   │   ├─→ Verify signature
│   │   ├─→ Check expiration
│   │   ├─→ Extract claims
│   │   └─→ Return JwtClaims
│   │
│   ├─→ UserRepository.findById(userId)
│   │   └─→ Return User (or empty)
│   │
│   └─→ Check: !user.getIsDeleted()
│       └─→ Return User
│
├─→ Request proceeds with user
│
└─→ Chain.doFilter(request, response)
```

---

## Error Handling Paths

### Path 1: Expired Token

```
Client sends request with old token

JwtService.validateTokenAndGetUser(token)
│
└─→ JwtTokenProvider.validateAndExtractClaims(token)
    │
    └─→ Claims expiration < now
        │
        └─→ throw JwtException("TOKEN_EXPIRED", "Token has expired")
            │
            └─→ Filter catches
                │
                └─→ response.setStatus(401)
                │
                └─→ response.write({ error: "TOKEN_EXPIRED" })
                    │
                    └─→ Client sees 401
                        │
                        └─→ Redirects to login
```

### Path 2: Invalid Signature

```
Client sends modified token

JwtService.validateTokenAndGetUser(token)
│
└─→ JwtTokenProvider.validateAndExtractClaims(token)
    │
    └─→ Jwts.parser().parseClaimsJws(token)
        │
        └─→ Signature verification fails
            │
            └─→ catch SignatureException
                │
                └─→ throw JwtException("TOKEN_INVALID", "Invalid token signature")
                    │
                    └─→ Filter catches
                        │
                        └─→ response.setStatus(401)
                            │
                            └─→ Client sees 401
```

### Path 3: User Deleted After Token Issued

```
User had valid token, but admin deletes account

Client sends valid token

JwtService.validateTokenAndGetUser(token)
│
├─→ JwtTokenProvider.validateAndExtractClaims(token)
│   └─→ Token valid (signature OK, not expired)
│       └─→ Return JwtClaims
│
├─→ UserRepository.findById(userId)
│   └─→ User found
│
├─→ Check: user.getIsDeleted() == true
│   │
│   └─→ throw JwtException("USER_DELETED", "User has been deleted")
        │
        └─→ Filter catches
            │
            └─→ response.setStatus(403)
                │
                └─→ Client sees 403 Forbidden
```

---

## Configuration for Different Environments

### Development

```yaml
jwt:
  secret: my-dev-secret-key-that-is-long-enough
  expiration: 86400000           # 24 hours
  refresh-expiration: 604800000  # 7 days

service:
  tenant-id: test-school
```

### Staging

```yaml
jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000           # 24 hours
  refresh-expiration: 604800000  # 7 days

service:
  tenant-id: ${TENANT_ID}
```

Environment variables:
```bash
JWT_SECRET="generated-secret-key-from-secure-vault"
TENANT_ID="staging-school"
```

### Production

```yaml
jwt:
  secret: ${JWT_SECRET}
  expiration: 3600000            # 1 hour (shorter for security)
  refresh-expiration: 86400000   # 1 day

service:
  tenant-id: ${TENANT_ID}
```

Environment variables:
```bash
JWT_SECRET="$(openssl rand -hex 32)"  # 256-bit secret
TENANT_ID="prod-school"
```

---

## Integration Checklist for Next Phase (Controllers)

- [ ] Inject JwtService into AuthenticationController
- [ ] Call jwtService.generateToken() after signIn()
- [ ] Include accessToken in SignInResponse
- [ ] Extract token from Authorization header in filter (future phase)
- [ ] Call jwtService.validateTokenAndGetUser() in filter
- [ ] Map JwtException to HTTP status codes:
  - [ ] TOKEN_EXPIRED → 401
  - [ ] TOKEN_INVALID → 401
  - [ ] USER_NOT_FOUND → 404
  - [ ] USER_DELETED → 403
- [ ] Include user in request context
- [ ] Use user in endpoint methods
- [ ] Add optional refresh endpoint (future)

---

## Performance Considerations

### Token Generation Cost
- ~50ms per token (HMAC-SHA512 + serialization)
- Called once per sign-in
- Not on every request
- Acceptable performance

### Token Validation Cost
- ~1ms per validation (signature verification)
- Called on every authenticated request
- No database lookup (stateless)
- Very fast

### Permission Checking Cost
- 0ms (already in token)
- No database query needed
- Permission list in memory
- Extremely fast

### Database Lookup Cost
- ~5-10ms for UserRepository.findById()
- Only done when validating token (to check if user deleted)
- Can be optimized with Redis cache (future)
- Acceptable for most use cases

---

## Future Enhancements

1. **Refresh Token Implementation**
   - Separate refresh token with longer expiry
   - Refresh endpoint to get new access token

2. **Token Blacklist**
   - Redis-backed blacklist for logout
   - Check blacklist on validation

3. **Key Rotation**
   - Multiple signing keys
   - Graceful key rollover

4. **Rate Limiting**
   - Limit token generation requests
   - Prevent brute-force attacks

5. **Audit Logging**
   - Log token generation
   - Log validation attempts
   - Track suspicious activity

6. **Token Encryption**
   - Encrypt claims (future if needed)
   - Currently signed, not encrypted
   - Appropriate for current use case

