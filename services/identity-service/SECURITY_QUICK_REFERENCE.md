# Spring Security & JWT Authentication - Quick Reference

## Implementation Summary

### 2 New Classes

1. **SecurityConfig.java** (100+ LOC)
   - Spring Security configuration
   - Endpoint authorization rules
   - Filter chain setup
   - Bean definitions

2. **JwtAuthenticationFilter.java** (100+ LOC)
   - JWT extraction from Authorization header
   - Token validation
   - SecurityContext population
   - Error handling

---

## Endpoint Access Control

| Endpoint | Method | Public | Protected | Auth Required |
|----------|--------|--------|-----------|---------------|
| /signup | POST | ✅ | ❌ | No |
| /signin | POST | ✅ | ❌ | No |
| /forgot-password | POST | ✅ | ❌ | No |
| /reset-password | POST | ✅ | ❌ | No |
| /signout | POST | ❌ | ✅ | Yes |
| /me | GET | ❌ | ✅ | Yes |

---

## How It Works

### Step 1: Request Arrives
```
GET /api/v1/auth/me
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

### Step 2: JwtAuthenticationFilter Runs
```
1. Extract token from "Bearer ..." header
2. Call jwtService.validateTokenAndGetUser(token)
3. If valid: Set User in SecurityContext
4. If invalid: Clear SecurityContext
5. Continue filter chain
```

### Step 3: Spring Security Authorization Filter Checks
```
1. Check rule: GET /api/v1/auth/me → authenticated()
2. Check SecurityContext: authenticated?
3. If yes: Allow request
4. If no: Send 401 Unauthorized
```

### Step 4: Controller Processes (if allowed)
```
Get user from SecurityContext or jwtService
Process request
Return response
```

---

## Configuration Details

### Public Endpoints (permitAll)

```java
.requestMatchers(POST, "/api/v1/auth/signup").permitAll()
.requestMatchers(POST, "/api/v1/auth/signin").permitAll()
.requestMatchers(POST, "/api/v1/auth/forgot-password").permitAll()
.requestMatchers(POST, "/api/v1/auth/reset-password").permitAll()
.requestMatchers(GET, "/actuator/health/**").permitAll()
.requestMatchers(GET, "/actuator/info").permitAll()
```

### Protected Endpoints (authenticated)

```java
.requestMatchers(POST, "/api/v1/auth/signout").authenticated()
.requestMatchers(GET, "/api/v1/auth/me").authenticated()
```

### Default (denyAll)

```java
.anyRequest().denyAll()
```

---

## Filter Integration

### Filter Registration

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    http
        .csrf().disable()
        .authorizeHttpRequests(...)
        .sessionManagement().sessionCreationPolicy(STATELESS)
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
}
```

### Filter Order

```
JwtAuthenticationFilter (CUSTOM)
    ↓
Spring Security Filters
    ↓
Authorization Filter
    ↓
Controller
```

---

## Request/Response Examples

### Public Endpoint: Sign In

**Request:**
```
POST /api/v1/auth/signin
Content-Type: application/json

{
  "username": "john_doe",
  "password": "SecureP@ss123"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "user": {...}
}
```

**Flow:**
1. No Authorization header
2. JwtAuthenticationFilter: skip (no token)
3. Spring Security: Check rule → permitAll → Allow
4. Controller: Process request

---

### Protected Endpoint: Get Current User

**Request (Valid Token):**
```
GET /api/v1/auth/me
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Response (200 OK):**
```json
{
  "id": "uuid",
  "username": "john_doe",
  "email": "john@example.com",
  "role": "TEACHER",
  "permissions": ["STUDENT_VIEW", "ATTENDANCE_MARK"],
  ...
}
```

**Flow:**
1. Authorization header present
2. JwtAuthenticationFilter: Extract token, validate, set SecurityContext
3. Spring Security: Check rule → authenticated() → Allow (auth present)
4. Controller: Get user from SecurityContext/service

---

### Protected Endpoint: Missing Token

**Request (No Token):**
```
GET /api/v1/auth/me
(No Authorization header)
```

**Response (401 Unauthorized):**
```
(Spring Security sends 401)
```

**Flow:**
1. No Authorization header
2. JwtAuthenticationFilter: skip (no token)
3. Spring Security: Check rule → authenticated() → Deny (no auth)
4. 401 Unauthorized response

---

### Protected Endpoint: Invalid Token

**Request (Invalid Token):**
```
GET /api/v1/auth/me
Authorization: Bearer invalid-token-xyz
```

**Response (401 Unauthorized):**
```
(Spring Security sends 401)
```

**Flow:**
1. Authorization header present
2. JwtAuthenticationFilter: Extract token, validation fails, clear SecurityContext
3. Spring Security: Check rule → authenticated() → Deny (no auth)
4. 401 Unauthorized response

---

## Session Management

### Stateless Configuration

```java
.sessionManagement()
    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
```

**Means:**
- No session cookies
- No JSESSIONID
- No server-side sessions
- Each request independently authenticated

---

## CSRF Protection

### Disabled for Stateless APIs

```java
.csrf().disable()
```

**Why:**
- CSRF is for session-based auth
- Stateless JWT doesn't need CSRF
- Authorization header provides protection

---

## Dependency Injection

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
}

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }
}
```

---

## Error Handling

### In Filter

```java
try {
    // Extract and validate token
    User user = jwtService.validateTokenAndGetUser(token);
    // Set SecurityContext
    SecurityContextHolder.getContext().setAuthentication(...);
} catch (JwtException e) {
    // Clear context on error
    SecurityContextHolder.clearContext();
} finally {
    // Always continue to next filter
    filterChain.doFilter(request, response);
}
```

**Note:** Filter doesn't send response. Spring Security sends 401/403 based on authorization rules.

---

## Testing

### Unit Test: Public Endpoint

```java
@Test
public void testPublicEndpointNoAuth() throws Exception {
    mockMvc.perform(post("/api/v1/auth/signin")
            .contentType(APPLICATION_JSON)
            .content(...))
        .andExpect(status().isOk());
}
```

### Unit Test: Protected Endpoint

```java
@Test
public void testProtectedEndpointNoAuth() throws Exception {
    mockMvc.perform(get("/api/v1/auth/me"))
        .andExpect(status().isUnauthorized());
}

@Test
public void testProtectedEndpointWithAuth() throws Exception {
    mockMvc.perform(get("/api/v1/auth/me")
            .header("Authorization", "Bearer valid-token"))
        .andExpect(status().isOk());
}
```

---

## Accessing User in Controller

### Option 1: SecurityContext (Spring way)

```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
User user = (User) auth.getPrincipal();
```

### Option 2: JwtService (Current implementation)

```java
User user = jwtService.validateTokenAndGetUser(authHeader);
```

---

## Security Checklist

✅ Stateless (no sessions)
✅ JWT validation on every request
✅ Token signature verified
✅ Token expiration checked
✅ User deletion checked
✅ Public endpoints: permitAll
✅ Protected endpoints: authenticated
✅ CSRF disabled
✅ Filter order correct
✅ Error handling
✅ SecurityContext populated

---

## What's Implemented

✅ SecurityConfig class
✅ HTTP authorization rules
✅ Session management (stateless)
✅ JWT filter
✅ Token extraction & validation
✅ SecurityContext population
✅ Error handling

---

## What's NOT Implemented (Future)

❌ Role-based authorization
❌ Permission enforcement
❌ Global exception handler (401/403)
❌ CORS configuration
❌ Method-level security (@PreAuthorize)
❌ Custom authentication provider
❌ Rate limiting
❌ Account lockout

---

## References

- **Full Implementation:** SECURITY_CONFIG_IMPLEMENTATION.md
- **JWT Service:** JwtService, JwtTokenProvider
- **Authentication Service:** AuthenticationService
- **Controllers:** AuthenticationController

---

## Status

✅ Spring Security configured
✅ JWT filter implemented
✅ All endpoints secured
✅ Stateless authentication ready
✅ Code compiles, zero errors
✅ Ready for testing & deployment

