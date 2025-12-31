# Spring Security Configuration with JWT Authentication

## Overview

The Spring Security configuration implements stateless JWT-based authentication for the identity-service.

**Key Features:**
- Stateless security (no sessions or cookies)
- JWT token validation on every request
- Public endpoints: signup, signin, forgot-password, reset-password
- Protected endpoints: signout, /me
- No role-based authorization yet (future phase)
- No permission enforcement yet (future phase)

---

## Architecture

```
HTTP Request
    ↓
Spring Security Filter Chain
    ├─ JwtAuthenticationFilter (FIRST)
    │  ├─ Extract token from Authorization header
    │  ├─ Validate token signature
    │  ├─ Check token expiration
    │  ├─ Fetch user from database
    │  ├─ Check user not deleted
    │  └─ Populate SecurityContext
    │
    ├─ Other Spring Security Filters
    │
    └─ AuthorizationFilter
       ├─ Check @authorizeHttpRequests rules
       ├─ Public endpoints: permit
       ├─ Protected endpoints: require authentication
       └─ Other endpoints: deny
    
    ↓
Controller
    ├─ Get authentication from SecurityContext
    ├─ Access User object
    └─ Process request
    
    ↓
HTTP Response
```

---

## Components

### 1. SecurityConfig (Spring Security Configuration)

**Location:** `src/main/java/com/school/identity/config/SecurityConfig.java`

**Responsibility:** 
- Configure HTTP security rules
- Define public/protected endpoints
- Set session management policy (stateless)
- Register JWT filter in filter chain
- Provide beans (AuthenticationManager, PasswordEncoder)

**Key Configurations:**

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    http
        .csrf().disable()                    // No CSRF (stateless JWT)
        .authorizeHttpRequests(authz -> authz
            .requestMatchers(POST, "/api/v1/auth/signup").permitAll()
            .requestMatchers(POST, "/api/v1/auth/signin").permitAll()
            .requestMatchers(POST, "/api/v1/auth/forgot-password").permitAll()
            .requestMatchers(POST, "/api/v1/auth/reset-password").permitAll()
            .requestMatchers(POST, "/api/v1/auth/signout").authenticated()
            .requestMatchers(GET, "/api/v1/auth/me").authenticated()
            .anyRequest().denyAll()
        )
        .sessionManagement()
            .sessionCreationPolicy(STATELESS)
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
}
```

### 2. JwtAuthenticationFilter (JWT Validation Filter)

**Location:** `src/main/java/com/school/identity/security/JwtAuthenticationFilter.java`

**Responsibility:**
- Extract JWT token from Authorization header
- Validate token signature and expiration
- Fetch user from database
- Populate SecurityContext with authenticated user
- Handle validation errors gracefully

**Filter Chain:**
```
1. Extract token from "Authorization: Bearer <token>" header
2. If no token: continue (public endpoint or Spring Security will reject)
3. If token invalid: clear context, continue (Spring Security will reject)
4. If token valid:
   - Fetch user from database
   - Check user not deleted
   - Create UsernamePasswordAuthenticationToken
   - Set in SecurityContext
5. Continue filter chain
```

---

## Endpoint Security Rules

### Public Endpoints (permitAll)

**Anyone can access without authentication:**

| Method | Path | Purpose |
|--------|------|---------|
| POST | /api/v1/auth/signup | User registration |
| POST | /api/v1/auth/signin | User authentication |
| POST | /api/v1/auth/forgot-password | Password reset request |
| POST | /api/v1/auth/reset-password | Complete password reset |

### Protected Endpoints (authenticated)

**Authentication required (valid JWT token):**

| Method | Path | Purpose |
|--------|------|---------|
| GET | /api/v1/auth/me | Get current user |
| POST | /api/v1/auth/signout | Logout |

### Denied Endpoints (denyAll)

**All other requests are denied by default.**

---

## Authentication Flow

### Scenario 1: Public Endpoint (POST /api/v1/auth/signin)

```
HTTP Request: POST /api/v1/auth/signin
Content-Type: application/json
{ "username": "john_doe", "password": "..." }

    ↓

JwtAuthenticationFilter
├─ No Authorization header
├─ Token = null
└─ Continue filter chain (don't set authentication)

    ↓

Spring Security Authorization Filter
├─ Check rule: POST /api/v1/auth/signin → permitAll()
├─ Allow request
└─ No authentication required

    ↓

AuthenticationController.signIn()
├─ Authenticate user
├─ Generate JWT
└─ Return token

    ↓

HTTP Response: 200 OK
{ "accessToken": "eyJ...", "user": {...} }
```

### Scenario 2: Protected Endpoint with Valid Token

```
HTTP Request: GET /api/v1/auth/me
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...

    ↓

JwtAuthenticationFilter
├─ Extract token from header
├─ Call jwtService.validateTokenAndGetUser()
│  ├─ Validate signature ✓
│  ├─ Check expiration ✓
│  ├─ Fetch user from DB ✓
│  └─ Check not deleted ✓
├─ Create UsernamePasswordAuthenticationToken
│  {
│    principal: User object,
│    credentials: null,
│    authorities: []
│  }
├─ Set in SecurityContext
└─ Continue filter chain

    ↓

Spring Security Authorization Filter
├─ Check rule: GET /api/v1/auth/me → authenticated()
├─ Check SecurityContext: authentication present ✓
├─ Allow request
└─ Continue

    ↓

AuthenticationController.getCurrentUser()
├─ Get authentication from SecurityContext
├─ Extract User from principal
├─ Extract permissions from roles
└─ Return user details

    ↓

HTTP Response: 200 OK
{ "id": "uuid", "username": "...", "permissions": [...], ... }
```

### Scenario 3: Protected Endpoint with Invalid Token

```
HTTP Request: GET /api/v1/auth/me
Authorization: Bearer invalid-token-xyz

    ↓

JwtAuthenticationFilter
├─ Extract token from header
├─ Call jwtService.validateTokenAndGetUser()
│  └─ Throw JwtException("TOKEN_INVALID")
├─ Catch exception
├─ Clear SecurityContext
└─ Continue filter chain

    ↓

Spring Security Authorization Filter
├─ Check rule: GET /api/v1/auth/me → authenticated()
├─ Check SecurityContext: no authentication
├─ Deny request
└─ Send 401 Unauthorized

    ↓

HTTP Response: 401 Unauthorized
(Response handled by Spring Security's exception handler)
```

### Scenario 4: Protected Endpoint with No Token

```
HTTP Request: GET /api/v1/auth/me
(No Authorization header)

    ↓

JwtAuthenticationFilter
├─ No Authorization header
├─ Token = null
├─ Continue filter chain
└─ Don't set authentication

    ↓

Spring Security Authorization Filter
├─ Check rule: GET /api/v1/auth/me → authenticated()
├─ Check SecurityContext: no authentication
├─ Deny request
└─ Send 401 Unauthorized

    ↓

HTTP Response: 401 Unauthorized
```

---

## SecurityContext Usage in Controllers

### Accessing Authenticated User in Controller

```java
@GetMapping("/me")
public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
    // Option 1: Get authentication from SecurityContext
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = (User) authentication.getPrincipal();
    
    // Option 2: Use authHeader parameter (simpler, already done in controller)
    User user = jwtService.validateTokenAndGetUser(authHeader);
    
    // Process with user object
    return ResponseEntity.ok(user);
}
```

**Note:** In the current implementation, controllers get User directly from `jwtService.validateTokenAndGetUser()` instead of from SecurityContext. Both approaches are valid:
- **Via SecurityContext:** More Spring-idiomatic, useful for authorization rules
- **Via JwtService:** More explicit, easier to test, current implementation

---

## Session Management

### Stateless Configuration

```java
.sessionManagement()
    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
```

**What this means:**
- No session cookies created
- No JSESSIONID tracking
- No server-side session storage
- Each request independently authenticated via JWT
- Suitable for microservices and REST APIs

**Benefits:**
- Scalable (no session affinity needed)
- Stateless (microservice-friendly)
- Token can be used across multiple servers
- No session fixation vulnerabilities

---

## CSRF Protection

### Disabled for Stateless APIs

```java
.csrf().disable()
```

**Why disabled:**
- CSRF (Cross-Site Request Forgery) protection requires session/cookies
- Stateless JWT doesn't use sessions
- JWT tokens in Authorization header have CORS protection

**Security Note:** Not disabled by default. In stateless APIs, CSRF protection is not applicable.

---

## Filter Order

### Spring Security Filter Chain Order

```
1. JwtAuthenticationFilter (CUSTOM - added first)
   ├─ Extracts and validates JWT
   └─ Populates SecurityContext
2. SecurityContextPersistenceFilter
3. HeaderWriterFilter
4. LogoutFilter
5. UsernamePasswordAuthenticationFilter (DEFAULT)
6. BasicAuthenticationFilter (DEFAULT)
7. ...
8. FilterSecurityInterceptor (Authorization check)
```

**Custom filter added BEFORE UsernamePasswordAuthenticationFilter** so JWT is processed before form-based authentication.

---

## Error Handling

### JWT Validation Errors

When JwtException is caught in filter:

1. Exception is caught
2. SecurityContext is cleared
3. Request continues to next filter
4. Spring Security Authorization Filter checks for authentication
5. If endpoint requires authentication: 401 Unauthorized
6. If endpoint is public: request continues to controller

**Note:** Filter doesn't send response. Spring Security handles error responses for authentication failures.

---

## Integration with Existing Services

### Dependencies

**JwtService:**
- `validateTokenAndGetUser(String token) -> User`
- `isTokenValid(String token) -> boolean`

**User Entity:**
- Must be serializable
- Contains all user details
- Returned by validateTokenAndGetUser()

---

## Configuration Properties

### application.yml

```yaml
jwt:
  secret: ${JWT_SECRET:dev-secret}
  expiration: ${JWT_EXPIRATION:86400000}

spring:
  security:
    # Default Spring Security properties
    user:
      name: # Not used (JWT auth only)
      password: # Not used (JWT auth only)
```

---

## Testing

### Unit Tests

```java
@WebMvcTest(AuthenticationController.class)
public class SecurityConfigTest {
    
    @Test
    public void testPublicEndpointAllowed() throws Exception {
        mockMvc.perform(post("/api/v1/auth/signin")
                .contentType(APPLICATION_JSON)
                .content(...))
            .andExpect(status().isOk());
    }
    
    @Test
    public void testProtectedEndpointRequiresAuth() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
            .andExpect(status().isUnauthorized());
    }
    
    @Test
    public void testProtectedEndpointWithValidToken() throws Exception {
        String token = "Bearer valid-jwt-token";
        mockMvc.perform(get("/api/v1/auth/me")
                .header("Authorization", token))
            .andExpect(status().isOk());
    }
}
```

### Integration Tests

```java
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityIntegrationTest {
    
    @Test
    public void testEndToEndWithJwt() throws Exception {
        // 1. SignIn to get token
        MvcResult signinResult = mockMvc.perform(post("/api/v1/auth/signin")...)
            .andExpect(status().isOk())
            .andReturn();
        
        String token = extractToken(signinResult);
        
        // 2. Use token to access protected endpoint
        mockMvc.perform(get("/api/v1/auth/me")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }
}
```

---

## Security Checklist

✅ Stateless session management (no cookies)
✅ JWT validation on every request
✅ Token signature verification
✅ Token expiration checking
✅ User deletion check (soft delete)
✅ Public endpoints: permitAll
✅ Protected endpoints: authenticated
✅ CSRF disabled (not applicable)
✅ Filter order correct (JWT before other auth)
✅ No sensitive data in logs
✅ Secure token extraction from header
✅ Graceful error handling
✅ SecurityContext properly set

---

## What's Implemented

✅ SecurityConfig class
✅ HTTP authorization rules
✅ Session management (stateless)
✅ JWT filter registration
✅ JwtAuthenticationFilter
✅ Token extraction from header
✅ Token validation
✅ SecurityContext population
✅ Error handling in filter

---

## What's NOT Implemented (Future Phases)

❌ Role-based authorization (RBAC)
❌ Permission enforcement
❌ Method-level security (@PreAuthorize)
❌ Global exception handler for 401/403
❌ CORS configuration
❌ Custom authentication provider
❌ RememberMe functionality
❌ Account lockout policies
❌ Rate limiting on auth endpoints

---

## Notes

### Why No Permission Enforcement Yet?

Permission enforcement requires:
1. Extracting permissions from token/user
2. Checking permission against endpoint
3. Returning 403 Forbidden if missing

This is better done in a separate phase with:
- `@PreAuthorize` annotations
- Custom permission evaluator
- Comprehensive testing

### Why SecurityContext is Populated

Even though controllers use `jwtService.validateTokenAndGetUser()`, we populate SecurityContext because:
1. Other Spring components may need it
2. Future authorization checks will use it
3. Spring-idiomatic approach
4. Better for auditing/logging

---

## Deployment Notes

### Environment Variables

```bash
JWT_SECRET="$(openssl rand -hex 32)"  # 256-bit secret
JWT_EXPIRATION=86400000               # 24 hours
```

### Production Checklist

- [ ] JWT_SECRET set to strong random value
- [ ] HTTPS enabled (JWT in Authorization header)
- [ ] Token expiration appropriate for use case
- [ ] Logging configured (but no tokens logged)
- [ ] Monitoring enabled
- [ ] Rate limiting deployed (separate component)

---

## References

- **OpenAPI Contract:** docs/api-contracts/identity-service.yaml
- **JWT Service:** JwtService documentation
- **Authentication Service:** AuthenticationService documentation
- **Spring Security Docs:** https://spring.io/projects/spring-security

---

## Status

✅ Spring Security configuration complete
✅ JWT authentication filter implemented
✅ All endpoints properly secured
✅ Stateless authentication ready
✅ Code compiles successfully
✅ Ready for testing and deployment

