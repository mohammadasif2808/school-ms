# REST Controllers - Integration & Testing Guide

## Layer Integration

```
┌─────────────────────────────────────────────────────────┐
│         HTTP Client (Frontend/Gateway)                  │
│  POST /api/v1/auth/signup                              │
│  POST /api/v1/auth/signin                              │
│  POST /api/v1/auth/signout                             │
│  GET  /api/v1/auth/me                                  │
└────────────────┬────────────────────────────────────────┘
                 │ HTTP requests
                 ▼
┌─────────────────────────────────────────────────────────┐
│    AuthenticationController (NEW)                       │
│    ├─ signUp() → 201/400/409                           │
│    ├─ signIn() → 200/401/403                           │
│    ├─ signOut() → 200/401                              │
│    └─ getCurrentUser() → 200/401/403/404                │
└────────────────┬────────────────────────────────────────┘
                 │ delegates to
         ┌───────┴──────────┐
         │                  │
         ▼                  ▼
    ┌──────────────┐   ┌──────────────┐
    │AuthService   │   │JwtService    │
    │(EXISTING)    │   │(EXISTING)    │
    ├─ signUp()   │   ├─ generateToken│
    ├─ signIn()   │   ├─ validateToken│
    └──────┬───────┘   └────────┬─────┘
           │                    │
           └────────┬───────────┘
                    │
                    ▼
           ┌─────────────────┐
           │UserRepository   │
           │RoleRepository   │
           │PermRepository   │
           └────────┬────────┘
                    │
                    ▼
           ┌─────────────────┐
           │MySQL Database   │
           └─────────────────┘
```

---

## Request/Response Flow Diagrams

### Sign Up Flow

```
Client
  │
  └─ POST /api/v1/auth/signup
     {
       username, email, password,
       first_name, last_name, phone
     }
     
     ▼
┌──────────────────────────────────┐
│ AuthenticationController.signUp()│
└──────────────┬───────────────────┘
               │
     ┌─────────┴─────────┐
     │ @Valid annotation │  ← Validates DTO
     │ (Bean Validation) │
     └─────────┬─────────┘
               │
     If invalid: return 400 BadRequest
     If valid: continue
               │
               ▼
┌──────────────────────────────────┐
│ authService.signUp(request)      │
├──────────────────────────────────┤
│ ├─ validateSignUpRequest()       │
│ ├─ checkUsername!exists()        │
│ ├─ checkEmail!exists()           │
│ ├─ validatePasswordStrength()    │
│ ├─ hash password                 │
│ ├─ create User entity            │
│ └─ save to DB                    │
└──────────────┬───────────────────┘
               │
     Exceptions:
     - ValidationException → 400
     - AuthenticationException → 409
               │
               ▼
         ┌──────────┐
         │User (JPA)│
         └──────┬───┘
                │
                ▼
     ┌──────────────────────────┐
     │ mapToSignUpResponse()    │
     │ (map User → DTO)        │
     └──────────┬───────────────┘
                │
                ▼
     ┌──────────────────────────┐
     │ ResponseEntity 201       │
     │ {id, username, email, ..}│
     └──────────────────────────┘
                │
                └─ Client receives response
```

### Sign In Flow

```
Client
  │
  └─ POST /api/v1/auth/signin
     { username, password }
     
     ▼
┌──────────────────────────────────┐
│ AuthenticationController.signIn() │
└──────────────┬───────────────────┘
               │
     ┌─────────┴──────────┐
     │ @Valid annotation  │  ← Validates DTO
     └─────────┬──────────┘
               │
               ▼
┌──────────────────────────────────┐
│ authService.signIn(request)      │
├──────────────────────────────────┤
│ ├─ validateSignInRequest()       │
│ ├─ findByUsername() or findByEmail│
│ ├─ checkNotDeleted()             │
│ ├─ validateUserStatus()          │
│ ├─ password.matches()            │
│ └─ return User                   │
└──────────────┬───────────────────┘
               │
     Exceptions:
     - ValidationException → 400
     - AuthenticationException:
       - INVALID_CREDENTIALS → 401
       - ACCOUNT_INACTIVE → 403
       - ACCOUNT_BLOCKED → 403
               │
               ▼
         ┌──────────┐
         │User (JPA)│
         └──────┬───┘
                │
                ▼
┌──────────────────────────────────┐
│ jwtService.generateToken(user)   │
├──────────────────────────────────┤
│ ├─ extractPermissionsFromRoles() │
│ ├─ extractPrimaryRole()          │
│ ├─ create JWT claims             │
│ ├─ sign with HMAC-SHA512         │
│ └─ return token string           │
└──────────────┬───────────────────┘
               │
               ▼
     ┌──────────────────────────────┐
     │ mapToSignInResponse()         │
     │ {token, user info}           │
     └──────────┬───────────────────┘
                │
                ▼
     ┌──────────────────────────────┐
     │ ResponseEntity 200           │
     │ {accessToken, user: {...}}   │
     └──────────────────────────────┘
                │
                └─ Client receives token
```

### Get Current User Flow

```
Client
  │
  └─ GET /api/v1/auth/me
     Authorization: Bearer eyJ...
     
     ▼
┌──────────────────────────────────────┐
│ AuthenticationController.getCurrentUser│
└──────────────┬──────────────────────┘
               │
     Check: Authorization header present?
               │
     If missing: return 401
     If present: continue
               │
               ▼
┌──────────────────────────────────┐
│ jwtService.validateTokenAndGetUser │
├──────────────────────────────────┤
│ ├─ validateAndExtractClaims()    │
│ │  ├─ parse token               │
│ │  ├─ verify signature           │
│ │  ├─ check expiration           │
│ │  └─ extract claims             │
│ ├─ userRepository.findById()     │
│ ├─ check !isDeleted              │
│ └─ return User entity            │
└──────────────┬───────────────────┘
               │
     Exceptions:
     - JwtException:
       - TOKEN_EXPIRED → 401
       - TOKEN_INVALID → 401
       - USER_NOT_FOUND → 404
       - USER_DELETED → 403
               │
               ▼
         ┌──────────┐
         │User (JPA)│
         └──────┬───┘
                │
     Extract from user:
     ├─ jwtService.extractPermissions()
     ├─ jwtService.extractPrimaryRole()
                │
                ▼
     ┌──────────────────────────────┐
     │ mapToCurrentUserResponse()   │
     │ {user, permissions, role}   │
     └──────────┬───────────────────┘
                │
                ▼
     ┌──────────────────────────────┐
     │ ResponseEntity 200           │
     │ {id, username, permissions..}│
     └──────────────────────────────┘
                │
                └─ Client receives user data
```

---

## Exception Handling Paths

### SignUp - Username Exists

```
signUp(request)
    │
    └─ authService.signUp()
       └─ existsByUsername() = true
          └─ throw AuthenticationException("USERNAME_EXISTS")
             
             ▼
             
         Controller catches
         │
         └─ HTTP 409 Conflict
            {error: "USERNAME_EXISTS", message: "..."}
```

### SignIn - Invalid Credentials

```
signIn(request)
    │
    └─ authService.signIn()
       ├─ findByUsername() = empty
       │  └─ throw AuthenticationException("INVALID_CREDENTIALS")
       │     ▼ HTTP 401 Unauthorized
       │
       └─ password.matches() = false
          └─ throw AuthenticationException("INVALID_CREDENTIALS")
             ▼ HTTP 401 Unauthorized
```

### Me - Expired Token

```
getCurrentUser(authHeader)
    │
    └─ jwtService.validateTokenAndGetUser()
       └─ validateAndExtractClaims()
          └─ token.exp < now
             └─ throw JwtException("TOKEN_EXPIRED")
                ▼ HTTP 401 Unauthorized
```

### Me - User Deleted

```
getCurrentUser(authHeader)
    │
    └─ jwtService.validateTokenAndGetUser()
       ├─ validateAndExtractClaims() ✓ OK
       ├─ userRepository.findById() ✓ Found
       └─ user.isDeleted == true
          └─ throw JwtException("USER_DELETED")
             ▼ HTTP 403 Forbidden
```

---

## Testing Examples

### Unit Test: SignUp Success

```java
@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest {
    
    @MockBean
    private AuthenticationService authenticationService;
    
    @MockBean
    private JwtService jwtService;
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testSignUpSuccess() throws Exception {
        // Arrange
        SignUpRequest request = new SignUpRequest();
        request.setUsername("john_doe");
        request.setEmail("john@example.com");
        request.setPassword("SecureP@ss123");
        request.setFirst_name("John");
        request.setLast_name("Doe");
        request.setPhone("+1234567890");
        
        User newUser = new User();
        newUser.setId(UUID.randomUUID());
        newUser.setUsername("john_doe");
        newUser.setEmail("john@example.com");
        
        when(authenticationService.signUp(any())).thenReturn(newUser);
        
        // Act
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("john_doe"))
            .andExpect(jsonPath("$.email").value("john@example.com"));
        
        // Assert
        verify(authenticationService).signUp(any());
    }
}
```

### Unit Test: SignIn Returns JWT

```java
@Test
public void testSignInReturnsToken() throws Exception {
    // Arrange
    SignInRequest request = new SignInRequest();
    request.setUsername("john_doe");
    request.setPassword("SecureP@ss123");
    
    User user = new User();
    user.setUsername("john_doe");
    user.setEmail("john@example.com");
    
    String token = "eyJhbGciOiJIUzUxMiJ9...";
    
    when(authenticationService.signIn(any())).thenReturn(user);
    when(jwtService.generateToken(user)).thenReturn(token);
    when(jwtService.extractPermissions(user)).thenReturn(List.of());
    
    // Act
    mockMvc.perform(post("/api/v1/auth/signin")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value(token));
    
    // Assert
    verify(jwtService).generateToken(user);
}
```

### Unit Test: SignIn Invalid Credentials

```java
@Test
public void testSignInInvalidCredentials() throws Exception {
    // Arrange
    SignInRequest request = new SignInRequest();
    request.setUsername("wrong_user");
    request.setPassword("wrong_pass");
    
    when(authenticationService.signIn(any()))
        .thenThrow(new AuthenticationException("INVALID_CREDENTIALS", "..."));
    
    // Act
    mockMvc.perform(post("/api/v1/auth/signin")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("INVALID_CREDENTIALS"));
}
```

### Integration Test: Full Signup → Signin Flow

```java
@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    public void testSignupThenSigninFlow() throws Exception {
        // 1. Sign up
        SignUpRequest signupRequest = new SignUpRequest();
        signupRequest.setUsername("testuser");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("SecureP@ss123");
        signupRequest.setFirst_name("Test");
        signupRequest.setLast_name("User");
        signupRequest.setPhone("+1234567890");
        
        MvcResult signupResult = mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
            .andExpect(status().isCreated())
            .andReturn();
        
        // 2. Verify user created in DB
        User savedUser = userRepository.findByUsername("testuser").orElseThrow();
        assertNotNull(savedUser);
        assertEquals("testuser", savedUser.getUsername());
        
        // 3. Sign in
        SignInRequest signinRequest = new SignInRequest();
        signinRequest.setUsername("testuser");
        signinRequest.setPassword("SecureP@ss123");
        
        MvcResult signinResult = mockMvc.perform(post("/api/v1/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signinRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andReturn();
        
        // Extract token
        String response = signinResult.getResponse().getContentAsString();
        String token = extractToken(response);
        
        // 4. Verify token works for /me
        mockMvc.perform(get("/api/v1/auth/me")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("testuser"));
    }
}
```

---

## HTTP Response Examples

### 201 Created (SignUp Success)

```
HTTP/1.1 201 Created
Content-Type: application/json

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

### 200 OK (SignIn Success)

```
HTTP/1.1 200 OK
Content-Type: application/json

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

### 200 OK (CurrentUser)

```
HTTP/1.1 200 OK
Content-Type: application/json

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

### 400 Bad Request (Validation Error)

```
HTTP/1.1 400 Bad Request
Content-Type: application/json

{
  "error": "VALIDATION_ERROR",
  "message": "Username is required"
}
```

### 409 Conflict (Username Exists)

```
HTTP/1.1 409 Conflict
Content-Type: application/json

{
  "error": "USERNAME_EXISTS",
  "message": "Username already exists"
}
```

### 401 Unauthorized (Invalid Credentials)

```
HTTP/1.1 401 Unauthorized
Content-Type: application/json

{
  "error": "INVALID_CREDENTIALS",
  "message": "Invalid username or password"
}
```

### 403 Forbidden (Account Blocked)

```
HTTP/1.1 403 Forbidden
Content-Type: application/json

{
  "error": "ACCOUNT_BLOCKED",
  "message": "User account is blocked"
}
```

### 401 Unauthorized (Token Expired)

```
HTTP/1.1 401 Unauthorized
Content-Type: application/json

{
  "error": "TOKEN_EXPIRED",
  "message": "Token has expired"
}
```

---

## cURL Examples

### SignUp

```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "SecureP@ss123",
    "first_name": "John",
    "last_name": "Doe",
    "phone": "+1234567890"
  }'
```

### SignIn

```bash
curl -X POST http://localhost:8080/api/v1/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "SecureP@ss123"
  }'
```

### Get Current User

```bash
curl -X GET http://localhost:8080/api/v1/auth/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

### SignOut

```bash
curl -X POST http://localhost:8080/api/v1/auth/signout \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

---

## What's Next

After REST Controllers:

1. **Global Exception Handler** (separate phase)
   - Centralize exception handling
   - Convert exceptions to HTTP responses
   - Custom error formatting

2. **JWT Filter** (separate phase)
   - Validate token on every request
   - Inject authenticated user into request context
   - Handle authorization

3. **Security Configuration** (separate phase)
   - Spring Security setup
   - Filter chain configuration
   - CORS, CSRF, etc.

---

## Controller Checklist

✅ SignUp endpoint (POST /api/v1/auth/signup)
✅ SignIn endpoint (POST /api/v1/auth/signin)
✅ SignOut endpoint (POST /api/v1/auth/signout)
✅ Get Current User endpoint (GET /api/v1/auth/me)
✅ Service delegation (no business logic)
✅ DTO validation (@Valid)
✅ Exception handling (specific catch blocks)
✅ Proper HTTP status codes
✅ Response DTOs match OpenAPI contract
✅ Constructor injection
✅ Helper methods for mapping
✅ JavaDoc comments

