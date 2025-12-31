# Spring Security Integration - Detailed Guide

## Complete Request/Response Flows

### Flow 1: Sign Up (Public Endpoint)

```
┌─────────────────────────────────────┐
│ Client                              │
│ POST /api/v1/auth/signup            │
│ Content-Type: application/json      │
│ {                                   │
│   "username": "john_doe",           │
│   "email": "john@example.com",      │
│   "password": "SecureP@ss123",      │
│   ...                               │
│ }                                   │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│ Spring Security Filter Chain        │
│                                     │
│ JwtAuthenticationFilter             │
│ ├─ Extract Authorization header    │
│ ├─ Header is null                  │
│ └─ Skip (no token to validate)     │
│                                     │
│ (Other Spring Security Filters)     │
│                                     │
│ Authorization Filter                │
│ ├─ Check rule for POST /signup     │
│ ├─ Rule: permitAll()               │
│ ├─ No authentication required      │
│ └─ Allow request                   │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│ AuthenticationController.signUp()   │
│ ├─ Receive SignUpRequest            │
│ ├─ Validate DTO (@Valid)            │
│ ├─ Call authService.signUp()        │
│ │  ├─ Validate fields               │
│ │  ├─ Check uniqueness              │
│ │  ├─ Hash password (BCrypt)        │
│ │  ├─ Create User entity            │
│ │  └─ Save to DB                    │
│ ├─ Map User → SignUpResponse        │
│ └─ Return 201 Created               │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│ Client Response: 201 Created        │
│ Content-Type: application/json      │
│ {                                   │
│   "id": "uuid",                     │
│   "username": "john_doe",           │
│   "email": "john@example.com",      │
│   "status": "ACTIVE",               │
│   "created_at": "2026-01-01T..."    │
│ }                                   │
└─────────────────────────────────────┘
```

---

### Flow 2: Sign In (Public Endpoint)

```
┌─────────────────────────────────────┐
│ Client                              │
│ POST /api/v1/auth/signin            │
│ Content-Type: application/json      │
│ {                                   │
│   "username": "john_doe",           │
│   "password": "SecureP@ss123"       │
│ }                                   │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│ Spring Security Filter Chain        │
│                                     │
│ JwtAuthenticationFilter             │
│ ├─ Extract Authorization header    │
│ ├─ Header is null                  │
│ └─ Skip (no token yet)              │
│                                     │
│ Authorization Filter                │
│ ├─ Check rule for POST /signin     │
│ ├─ Rule: permitAll()               │
│ └─ Allow request                   │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│ AuthenticationController.signIn()   │
│ ├─ Receive SignInRequest            │
│ ├─ Validate DTO (@Valid)            │
│ ├─ Call authService.signIn()        │
│ │  ├─ Find user by username/email   │
│ │  ├─ Verify password (BCrypt)      │
│ │  ├─ Check status                  │
│ │  └─ Return User entity            │
│ ├─ Call jwtService.generateToken()  │
│ │  ├─ Extract permissions           │
│ │  ├─ Create claims                 │
│ │  ├─ Sign with HMAC-SHA512         │
│ │  └─ Return JWT string             │
│ ├─ Map to SignInResponse            │
│ └─ Return 200 OK + token            │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│ Client Response: 200 OK             │
│ Content-Type: application/json      │
│ {                                   │
│   "accessToken":                    │
│     "eyJhbGciOiJIUzUxMiJ9...",    │
│   "user": {                         │
│     "id": "uuid",                   │
│     "username": "john_doe",         │
│     "email": "john@example.com",    │
│     "status": "ACTIVE"              │
│   }                                 │
│ }                                   │
└─────────────────────────────────────┘

Client stores JWT token for future requests
```

---

### Flow 3: Get Current User (Protected Endpoint, Valid Token)

```
┌──────────────────────────────────────┐
│ Client                               │
│ GET /api/v1/auth/me                  │
│ Authorization: Bearer                │
│   eyJhbGciOiJIUzUxMiJ9...           │
└────────────┬─────────────────────────┘
             │
             ▼
┌──────────────────────────────────────┐
│ Spring Security Filter Chain         │
│                                      │
│ JwtAuthenticationFilter              │
│ ├─ Extract Authorization header     │
│ ├─ Header: "Bearer eyJh..."         │
│ ├─ Call jwtService.                 │
│ │  validateTokenAndGetUser()         │
│ │  ├─ Extract token string           │
│ │  ├─ Parse JWT (JJWT library)       │
│ │  ├─ Verify signature               │
│ │  │  └─ HMAC-SHA512                 │
│ │  │  └─ Using JWT_SECRET            │
│ │  │  └─ Match? ✓                    │
│ │  ├─ Check expiration               │
│ │  │  └─ iat + expiry > now? ✓       │
│ │  ├─ Extract userId from claims     │
│ │  ├─ Call userRepository.           │
│ │  │  findById(userId)               │
│ │  │  └─ DB query                    │
│ │  │  └─ Return User                 │
│ │  ├─ Check user.isDeleted           │
│ │  │  └─ false? ✓                    │
│ │  └─ Return User entity             │
│ ├─ Create UsernamePasswordAuth...    │
│ │  Token                             │
│ │  ├─ principal: User                │
│ │  ├─ credentials: null              │
│ │  └─ authorities: []                │
│ ├─ Set in SecurityContext            │
│ └─ Continue filter chain             │
│                                      │
│ Authorization Filter                 │
│ ├─ Check rule: GET /me              │
│ ├─ Rule: authenticated()             │
│ ├─ Check SecurityContext             │
│ │  └─ authentication present? ✓      │
│ └─ Allow request                     │
└────────────┬─────────────────────────┘
             │
             ▼
┌──────────────────────────────────────┐
│ AuthenticationController.             │
│ getCurrentUser()                      │
│ ├─ Extract token from header         │
│ ├─ Call jwtService.                  │
│ │  validateTokenAndGetUser()         │
│ │  └─ Return User (already done)     │
│ ├─ Call jwtService.                  │
│ │  extractPermissions(user)          │
│ │  └─ Return List<String>            │
│ ├─ Call jwtService.                  │
│ │  extractPrimaryRole(user)          │
│ │  └─ Return String                  │
│ ├─ Map to CurrentUserResponse        │
│ └─ Return 200 OK                     │
└────────────┬─────────────────────────┘
             │
             ▼
┌──────────────────────────────────────┐
│ Client Response: 200 OK              │
│ Content-Type: application/json       │
│ {                                    │
│   "id": "uuid",                      │
│   "username": "john_doe",            │
│   "email": "john@example.com",       │
│   "role": "TEACHER",                 │
│   "permissions": [                   │
│     "STUDENT_VIEW",                  │
│     "ATTENDANCE_MARK"                │
│   ],                                 │
│   "status": "ACTIVE",                │
│   "created_at": "2026-01-01T..."     │
│ }                                    │
└──────────────────────────────────────┘
```

---

### Flow 4: Get Current User (Invalid Token)

```
┌──────────────────────────────────────┐
│ Client                               │
│ GET /api/v1/auth/me                  │
│ Authorization: Bearer invalid-xyz    │
└────────────┬─────────────────────────┘
             │
             ▼
┌──────────────────────────────────────┐
│ Spring Security Filter Chain         │
│                                      │
│ JwtAuthenticationFilter              │
│ ├─ Extract Authorization header     │
│ ├─ Header: "Bearer invalid-xyz"     │
│ ├─ Call jwtService.                 │
│ │  validateTokenAndGetUser()         │
│ │  └─ JwtException thrown!           │
│ │     ("TOKEN_INVALID")              │
│ ├─ Catch exception                   │
│ ├─ Clear SecurityContext             │
│ └─ Continue filter chain             │
│                                      │
│ Authorization Filter                 │
│ ├─ Check rule: GET /me              │
│ ├─ Rule: authenticated()             │
│ ├─ Check SecurityContext             │
│ │  └─ authentication present? ✗      │
│ ├─ Deny request                      │
│ └─ Send 401 Unauthorized             │
└────────────┬─────────────────────────┘
             │
             ▼
┌──────────────────────────────────────┐
│ Client Response: 401 Unauthorized    │
│                                      │
│ (Spring Security error response)     │
└──────────────────────────────────────┘
```

---

### Flow 5: Get Current User (No Token)

```
┌──────────────────────────────────────┐
│ Client                               │
│ GET /api/v1/auth/me                  │
│ (No Authorization header)            │
└────────────┬─────────────────────────┘
             │
             ▼
┌──────────────────────────────────────┐
│ Spring Security Filter Chain         │
│                                      │
│ JwtAuthenticationFilter              │
│ ├─ Extract Authorization header     │
│ ├─ Header is null                   │
│ ├─ token = null                     │
│ ├─ Skip validation                   │
│ └─ Continue filter chain             │
│    (Don't set authentication)        │
│                                      │
│ Authorization Filter                 │
│ ├─ Check rule: GET /me              │
│ ├─ Rule: authenticated()             │
│ ├─ Check SecurityContext             │
│ │  └─ authentication present? ✗      │
│ ├─ Deny request                      │
│ └─ Send 401 Unauthorized             │
└────────────┬─────────────────────────┘
             │
             ▼
┌──────────────────────────────────────┐
│ Client Response: 401 Unauthorized    │
│                                      │
│ (Spring Security error response)     │
└──────────────────────────────────────┘
```

---

## Endpoint Security Rules

### SecurityFilterChain Configuration

```java
authorizeHttpRequests(authz -> authz
    // Public endpoints
    .requestMatchers(POST, "/api/v1/auth/signup")
        .permitAll()
    .requestMatchers(POST, "/api/v1/auth/signin")
        .permitAll()
    .requestMatchers(POST, "/api/v1/auth/forgot-password")
        .permitAll()
    .requestMatchers(POST, "/api/v1/auth/reset-password")
        .permitAll()
    
    // Health checks (optional public)
    .requestMatchers("/actuator/health/**")
        .permitAll()
    .requestMatchers("/actuator/info")
        .permitAll()
    
    // Protected endpoints
    .requestMatchers(POST, "/api/v1/auth/signout")
        .authenticated()
    .requestMatchers(GET, "/api/v1/auth/me")
        .authenticated()
    
    // Everything else denied
    .anyRequest()
        .denyAll()
)
```

---

## JwtAuthenticationFilter Logic

### Token Extraction

```java
String extractTokenFromRequest(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    
    // Check: header exists?
    if (authHeader == null || authHeader.isEmpty()) {
        return null;  // No token
    }
    
    // Check: starts with "Bearer "?
    if (!authHeader.startsWith("Bearer ")) {
        return null;  // Invalid format
    }
    
    // Extract: substring after "Bearer "
    return authHeader.substring(7);
}
```

### Validation & SecurityContext Population

```java
protected void doFilterInternal(...) throws ServletException, IOException {
    try {
        // 1. Extract token
        String token = extractTokenFromRequest(request);
        
        // 2. If no token, skip
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 3. Validate token & get user
        User user = jwtService.validateTokenAndGetUser(token);
        
        // 4. Create authentication
        UsernamePasswordAuthenticationToken auth = 
            new UsernamePasswordAuthenticationToken(
                user,           // principal
                null,           // credentials
                new ArrayList() // authorities (empty for now)
            );
        
        // 5. Set in SecurityContext
        SecurityContextHolder.getContext().setAuthentication(auth);
        
    } catch (JwtException e) {
        // Validation failed
        SecurityContextHolder.clearContext();
    } catch (Exception e) {
        // Unexpected error
        SecurityContextHolder.clearContext();
    }
    
    // 6. Continue to next filter (always)
    filterChain.doFilter(request, response);
}
```

---

## SecurityContext Usage

### In Controllers

```java
@GetMapping("/me")
public ResponseEntity<?> getCurrentUser(
    @RequestHeader("Authorization") String authHeader
) {
    // Option 1: From SecurityContext (Spring way)
    Authentication authentication = 
        SecurityContextHolder.getContext().getAuthentication();
    User user = (User) authentication.getPrincipal();
    
    // Option 2: From JwtService (current implementation)
    User user = jwtService.validateTokenAndGetUser(authHeader);
    
    // Use user
    return ResponseEntity.ok(user);
}
```

### In Services

```java
@Service
public class SomeService {
    public void doSomething() {
        // Get authenticated user
        Authentication auth = 
            SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            User user = (User) auth.getPrincipal();
            // Use user info
        }
    }
}
```

---

## Testing Patterns

### Mock Authenticated User

```java
@Test
public void testProtectedEndpoint() throws Exception {
    String token = "valid-jwt-token";
    
    mockMvc.perform(get("/api/v1/auth/me")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk());
}
```

### Mock JwtService

```java
@MockBean
private JwtService jwtService;

@Test
public void testWithMockedJwt() throws Exception {
    User mockUser = new User();
    mockUser.setId(UUID.randomUUID());
    mockUser.setUsername("john_doe");
    
    when(jwtService.validateTokenAndGetUser(anyString()))
        .thenReturn(mockUser);
    
    mockMvc.perform(get("/api/v1/auth/me")
            .header("Authorization", "Bearer xyz"))
        .andExpect(status().isOk());
}
```

### Integration Test

```java
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityIntegrationTest {
    
    @Test
    public void testEndToEndFlow() throws Exception {
        // 1. Sign up
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(APPLICATION_JSON)
                .content(...))
            .andExpect(status().isCreated());
        
        // 2. Sign in
        MvcResult result = mockMvc.perform(post("/api/v1/auth/signin")
                .contentType(APPLICATION_JSON)
                .content(...))
            .andExpect(status().isOk())
            .andReturn();
        
        // Extract token
        String response = result.getResponse().getContentAsString();
        String token = JsonPath.read(response, "$.accessToken");
        
        // 3. Access protected endpoint
        mockMvc.perform(get("/api/v1/auth/me")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }
}
```

---

## Stateless vs Stateful Comparison

| Aspect | Stateless (JWT) | Stateful (Session) |
|--------|-----------------|-------------------|
| Storage | Client (token) | Server (session) |
| Scalability | Better (no affinity) | Requires affinity |
| Cookies | No | Yes (JSESSIONID) |
| CSRF | N/A | Required |
| Revocation | Only on expiry | Immediate |
| Multi-device | Easy (token) | Complex |

**Identity-service uses:** Stateless JWT

---

## Filter Chain Diagram

```
HTTP Request
    │
    ├─ SecurityContextPersistenceFilter
    │  └─ (empty, stateless)
    │
    ├─ HeaderWriterFilter
    │  └─ (security headers)
    │
    ├─ LogoutFilter
    │  └─ (logout handling)
    │
    ├─ JwtAuthenticationFilter (CUSTOM)
    │  ├─ Extract token
    │  ├─ Validate
    │  └─ Set SecurityContext
    │
    ├─ UsernamePasswordAuthenticationFilter
    │  └─ (form login, not used)
    │
    ├─ BasicAuthenticationFilter
    │  └─ (HTTP basic, not used)
    │
    ├─ ExceptionTranslationFilter
    │  └─ (error handling)
    │
    └─ FilterSecurityInterceptor
       ├─ Authorization check
       ├─ permitAll? → Allow
       ├─ authenticated? + auth present? → Allow
       └─ Otherwise → Deny
    
    ▼
Controller
```

---

## Deployment Checklist

- [ ] JWT_SECRET set (strong random value)
- [ ] HTTPS enabled
- [ ] Token expiration appropriate
- [ ] Logging configured (no tokens in logs)
- [ ] Monitoring enabled
- [ ] Error handling in place
- [ ] CORS configured (if needed)
- [ ] Tests pass
- [ ] Load testing done

---

## Status

✅ Spring Security configured
✅ JWT filter implemented
✅ All endpoints secured
✅ Stateless authentication ready
✅ Code compiles, zero errors
✅ Ready for testing & deployment

