# AuthService Architecture & Data Flow

## Layer Architecture

```
┌─────────────────────────────────────────┐
│         HTTP Controllers (Future)       │  ← Not yet implemented
│    AuthenticationController             │    Waiting for WORKFLOW 2
│  - POST /api/v1/auth/signup            │    Controller phase
│  - POST /api/v1/auth/signin            │
└─────────────────┬───────────────────────┘
                  │
                  ▼ (injects)
┌─────────────────────────────────────────┐
│     Business Logic (IMPLEMENTED)         │
│    AuthenticationService                │
│  ✅ signUp()                            │
│  ✅ signIn()                            │
│  ✅ Helper methods                      │
│  ✅ Validation methods                  │
│  ✅ Password hashing                    │
│  ✅ Status checks                       │
└─────────┬───────────────────────────────┘
          │
          ├─→ (injects)
          │   ┌────────────────────────────┐
          │   │  PasswordEncoder           │
          │   │  (BCryptPasswordEncoder)   │
          │   │  - encode()                │
          │   │  - matches()               │
          │   └────────────────────────────┘
          │
          └─→ (injects)
              ┌────────────────────────────┐
              │    UserRepository          │
              │  (Spring Data JPA)         │
              │  - findByUsername()        │
              │  - findByEmail()           │
              │  - existsByUsername()      │
              │  - existsByEmail()         │
              │  - save()                  │
              └────┬─────────────────────────┘
                   │
                   ▼
    ┌──────────────────────────────────┐
    │   MySQL Database                 │
    │  ┌──────────────────────────────┐│
    │  │      users table             ││
    │  │  - id (UUID)                 ││
    │  │  - username (UNIQUE)         ││
    │  │  - email (UNIQUE)            ││
    │  │  - password_hash (BCrypt)    ││
    │  │  - first_name                ││
    │  │  - last_name                 ││
    │  │  - phone                     ││
    │  │  - avatar_url                ││
    │  │  - is_super_admin            ││
    │  │  - status (ACTIVE/INACTIVE..)││
    │  │  - is_deleted                ││
    │  │  - created_at, updated_at    ││
    │  └──────────────────────────────┘│
    └──────────────────────────────────┘
```

---

## Sign Up Data Flow

```
┌─────────────────┐
│ SignUpRequest   │
│ (DTO)           │
│ - username      │
│ - email         │
│ - password      │  (plaintext)
│ - first_name    │
│ - last_name     │
│ - phone         │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────────────────┐
│  authService.signUp(request)            │
└────┬────────────────────────────────────┘
     │
     ├─ validateSignUpRequest()
     │  └─ Check: not null, all fields present
     │
     ├─ userRepository.existsByUsername()
     │  └─ DB Query: SELECT COUNT(*) FROM users WHERE username = ?
     │
     ├─ userRepository.existsByEmail()
     │  └─ DB Query: SELECT COUNT(*) FROM users WHERE email = ?
     │
     ├─ validatePasswordStrength()
     │  └─ Regex: ^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])...
     │
     ├─ passwordEncoder.encode(plaintext_password)
     │  └─ BCrypt hashing with cost=12
     │     Input:  "SecureP@ss123"
     │     Output: "$2a$12$abcdef...xyz" (always different)
     │
     ├─ Create User entity
     │  └─ username, email, firstName, lastName, phone
     │  └─ passwordHash (from bcrypt)
     │  └─ status = ACTIVE
     │  └─ isSuperAdmin = false
     │  └─ isDeleted = false
     │
     ├─ userRepository.save(user)
     │  └─ DB Insert: INSERT INTO users (...)
     │
     └─ Return User entity
        │
        ▼
    ┌─────────────────┐
    │ UserResponse    │
    │ (DTO)           │
    │ - id (UUID)     │
    │ - username      │
    │ - email         │
    │ - first_name    │
    │ - last_name     │
    │ - phone         │
    │ - status        │
    │ - created_at    │
    │                 │
    │ Note:           │
    │ ⚠️ NO password  │
    │ ⚠️ NO token     │
    └─────────────────┘

┌──────────────────────────────────────────────┐
│ Exception Handling                           │
│                                              │
│ if !validation:   VALIDATION_ERROR           │
│ if username_dup:  USERNAME_EXISTS            │
│ if email_dup:     EMAIL_EXISTS               │
│ if weak_pwd:      PASSWORD_WEAK              │
└──────────────────────────────────────────────┘
```

---

## Sign In Data Flow

```
┌─────────────────┐
│ SignInRequest   │
│ (DTO)           │
│ - username      │ (can be username or email)
│ - password      │ (plaintext)
└────────┬────────┘
         │
         ▼
┌──────────────────────────────────────────┐
│  authService.signIn(request)             │
└────┬─────────────────────────────────────┘
     │
     ├─ validateSignInRequest()
     │  └─ Check: not null, all fields present
     │
     ├─ userRepository.findByUsername(input)
     │  └─ DB Query: SELECT * FROM users WHERE username = ?
     │
     ├─ OR userRepository.findByEmail(input)
     │  └─ DB Query: SELECT * FROM users WHERE email = ?
     │
     ├─ Check user not found
     │  └─ Throw: INVALID_CREDENTIALS (don't reveal which one)
     │
     ├─ Check isDeleted != true
     │  └─ Soft-deleted users cannot sign in
     │  └─ Throw: INVALID_CREDENTIALS (security)
     │
     ├─ validateUserStatus(user)
     │  │
     │  ├─ if status == INACTIVE
     │  │  └─ Throw: ACCOUNT_INACTIVE
     │  │
     │  └─ if status == BLOCKED
     │     └─ Throw: ACCOUNT_BLOCKED
     │
     ├─ passwordEncoder.matches(plaintext, hash)
     │  │
     │  ├─ Input 1: "SecureP@ss123" (from request)
     │  ├─ Input 2: "$2a$12$abcdef...xyz" (from DB)
     │  └─ Output:  true/false (constant-time comparison)
     │
     ├─ if !matches()
     │  └─ Throw: INVALID_CREDENTIALS
     │
     └─ Return User entity
        │
        ▼
    ┌──────────────────────────────┐
    │ SignInResponse               │
    │ (DTO)                        │
    │ - accessToken (JWT)          │ ← Generated in next step
    │ - user:                      │
    │   - id (UUID)                │
    │   - username                 │
    │   - email                    │
    │   - first_name               │
    │   - last_name                │
    │   - avatar_url               │
    │   - status                   │
    │                              │
    │ Note:                        │
    │ ⚠️ NO password               │
    │ ⚠️ JWT generation not here   │
    └──────────────────────────────┘

┌────────────────────────────────────────────┐
│ Exception Handling                         │
│                                            │
│ if !validation:      VALIDATION_ERROR      │
│ if !found or pwd:    INVALID_CREDENTIALS   │
│ if status INACTIVE:  ACCOUNT_INACTIVE      │
│ if status BLOCKED:   ACCOUNT_BLOCKED       │
└────────────────────────────────────────────┘
```

---

## Password Hashing (BCrypt)

```
┌──────────────────────────────────────────────┐
│ BCryptPasswordEncoder (cost = 12)            │
└────────────────────┬─────────────────────────┘
                     │
                ┌────┴────┐
                │          │
                ▼          ▼
         ┌──────────┐ ┌──────────┐
         │ encode() │ │ matches()│
         └────┬─────┘ └────┬─────┘
              │            │
              │            └─ verify(plaintext, hash)
              │               Return: true/false
              │
              └─ hash(plaintext, salt)
                 1. Generate random salt
                 2. Hash plaintext with salt
                 3. Perform 2^12 rounds (cost=12)
                 4. Return: $2a$12$salt$hash

Example:
Input:  "SecureP@ss123"
Hash1:  "$2a$12$oCDqRx5.xyq7H1tVE3rUKuqfD8VmLWlpgP5vD8e7x7x7x7x7x7x7x"
Hash2:  "$2a$12$oCDqRx5.xyq7H1tVE3rUKOxH6K8z8z8z8z8z8z8z8z8z8z8z8z8z8z"
Hash3:  "$2a$12$oCDqRx5.xyq7H1tVE3rUKu2J9L3a3a3a3a3a3a3a3a3a3a3a3a3a3a"

All different! (due to unique salt + cost factor)

Verification:
matches("SecureP@ss123", Hash1) → true  ✅
matches("SecureP@ss123", Hash2) → true  ✅
matches("securepass123", Hash1) → false ❌
```

---

## User Status Validation

```
┌────────────────────────┐
│  User.status (enum)    │
├────────────────────────┤
│ ACTIVE        ✅ OK    │
│ INACTIVE      ❌ ERROR │ → ACCOUNT_INACTIVE
│ BLOCKED       ❌ ERROR │ → ACCOUNT_BLOCKED
└────────────────────────┘

Checked during: signIn()
Not checked during: signUp() (new users always ACTIVE)
Can be changed by: Admin endpoints (future)
```

---

## Soft Delete Handling

```
User Entity:
├─ isDeleted = false  → Active user
└─ isDeleted = true   → Soft-deleted (hidden)

Queries always check:
├─ signUp:     No check (not a condition)
├─ signIn:     Verify not deleted → INVALID_CREDENTIALS
├─ Helper:     findByUsernameAndIsDeletedFalse()
└─ Helper:     findByEmailAndIsDeletedFalse()

No permanent deletion:
└─ is_deleted just flags as hidden
└─ Data remains in database forever
└─ Soft deletion preserves referential integrity
```

---

## Exception Hierarchy

```
Throwable
├─ Exception
│  ├─ RuntimeException
│  │  ├─ AuthenticationException (custom)
│  │  │  └─ Properties:
│  │  │     - errorCode (String)
│  │  │     - message (String)
│  │  │     - cause (Throwable)
│  │  │
│  │  └─ ValidationException (custom)
│  │     └─ Properties:
│  │        - errorCode (String)
│  │        - message (String)
│  │        - cause (Throwable)
│  │
│  └─ ... other Spring exceptions
```

---

## Dependency Injection

```
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    // ✅ Constructor injection (required by AI_RULES.md)
    // ❌ No field injection (@Autowired)
    // ❌ No @Inject
}

@Configuration
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    
    // ✅ PasswordEncoder bean provided by config
}
```

---

## Error Code Reference

| Error Code | HTTP | Layer | Cause |
|-----------|------|-------|-------|
| VALIDATION_ERROR | 400 | DTO/Service | Required field missing/empty |
| USERNAME_EXISTS | 409 | Service | Username already registered |
| EMAIL_EXISTS | 409 | Service | Email already registered |
| PASSWORD_WEAK | 400 | Service | Weak password (complexity) |
| INVALID_CREDENTIALS | 401 | Service | User not found / password mismatch |
| ACCOUNT_INACTIVE | 403 | Service | User status is INACTIVE |
| ACCOUNT_BLOCKED | 403 | Service | User status is BLOCKED |

---

## Next Steps in WORKFLOW 2

```
Current: Service Layer (✅ DONE)
  ├─ signUp()
  ├─ signIn()
  ├─ Validation
  └─ Hashing

Next: Controller Layer (❌ TODO)
  ├─ AuthenticationController
  ├─ Endpoint mapping
  ├─ Exception handling
  └─ DTO mapping

Then: JWT Layer (❌ TODO)
  ├─ Token generation
  ├─ Token validation
  └─ Claims mapping

Finally: Security Config (❌ TODO)
  ├─ Filters
  ├─ Interceptors
  └─ CORS/Authentication
```

