# AuthenticationService - Implementation Documentation

## Overview

The `AuthenticationService` implements core authentication business logic for the identity-service, handling user registration and authentication with password hashing, validation, and user status checks.

## Location
`src/main/java/com/school/identity/service/AuthenticationService.java`

---

## Core Responsibilities

### 1. User Registration (Sign Up)
**Method:** `signUp(SignUpRequest signUpRequest) -> User`

**Purpose:** Register a new user account

**Process:**
1. Validate all required fields are present and not empty
2. Check username uniqueness (case-sensitive, database constraint)
3. Check email uniqueness (case-sensitive, database constraint)
4. Validate password strength (uppercase, lowercase, digit, special char, min 8 chars)
5. Create User entity with:
   - Username, email, first_name, last_name, phone
   - BCrypt-hashed password (cost factor: 12)
   - Status: ACTIVE (default)
   - is_super_admin: false (default)
   - is_deleted: false (default)
6. Persist to database

**Error Codes:**
- `VALIDATION_ERROR` — Required field missing or empty
- `USERNAME_EXISTS` — Username already registered
- `EMAIL_EXISTS` — Email already registered
- `PASSWORD_WEAK` — Password doesn't meet complexity requirements

**Returns:** User entity on success

---

### 2. User Authentication (Sign In)
**Method:** `signIn(SignInRequest signInRequest) -> User`

**Purpose:** Authenticate user and retrieve user entity (JWT generation handled separately)

**Process:**
1. Validate username and password fields are present and not empty
2. Query database for user by username first, then by email if not found
3. Check if user is soft-deleted (is_deleted == true)
4. Validate user account status:
   - ACTIVE — allowed
   - INACTIVE — throw ACCOUNT_INACTIVE
   - BLOCKED — throw ACCOUNT_BLOCKED
5. Verify provided password against bcrypt hash using PasswordEncoder
6. Return User entity if all checks pass

**Error Codes:**
- `VALIDATION_ERROR` — Required field missing or empty
- `INVALID_CREDENTIALS` — User not found OR password mismatch (generic for security)
- `ACCOUNT_INACTIVE` — User account is inactive
- `ACCOUNT_BLOCKED` — User account is blocked

**Returns:** Authenticated User entity on success

---

### 3. Password Hashing

**Algorithm:** BCrypt with cost factor 12

**Implementation:** `PasswordEncoder` bean in `SecurityConfig`

**Security Notes:**
- Cost factor 12 provides strong security against brute-force attacks
- Hash is stored in `user.passwordHash` column
- Original password is NEVER stored
- Same plaintext password produces different hashes each time (salt-based)

---

### 4. User Status Validation

**Supported Statuses:** `ACTIVE`, `INACTIVE`, `BLOCKED`

**Behavior:**
- **ACTIVE** — User can sign in
- **INACTIVE** — User cannot sign in, throws `ACCOUNT_INACTIVE`
- **BLOCKED** — User cannot sign in, throws `ACCOUNT_BLOCKED`

**Admin Tools:** Other services can change user status to manage access

---

## Helper Methods

### Public Methods

#### `userExists(String username) -> boolean`
Check if a non-deleted user exists by username

#### `emailExists(String email) -> boolean`
Check if a non-deleted user exists by email

#### `getUserByUsername(String username) -> Optional<User>`
Retrieve a non-deleted user by username

#### `getUserByEmail(String email) -> Optional<User>`
Retrieve a non-deleted user by email

#### `getUserById(UUID userId) -> Optional<User>`
Retrieve a non-deleted user by ID

---

## Private Validation Methods

### `validateSignUpRequest(SignUpRequest request)`
Checks:
- Request object is not null
- username is not blank
- email is not blank
- password is not blank
- first_name is not blank
- last_name is not blank
- phone is not blank

### `validateSignInRequest(SignInRequest request)`
Checks:
- Request object is not null
- username is not blank
- password is not blank

### `validatePasswordStrength(String password)`
Regex pattern: `^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$`

Requirements:
- At least 1 uppercase letter (A-Z)
- At least 1 lowercase letter (a-z)
- At least 1 digit (0-9)
- At least 1 special character (@$!%*?&)
- Minimum 8 characters total

### `validateUserStatus(User user)`
Checks:
- status != INACTIVE (throws ACCOUNT_INACTIVE)
- status != BLOCKED (throws ACCOUNT_BLOCKED)

---

## Exception Handling

### Custom Exceptions

#### `AuthenticationException`
**Package:** `com.school.identity.exception`

Thrown for authentication failures:
- Invalid credentials
- Account inactive/blocked
- Username/email already exists

**Properties:**
- `errorCode` — Machine-readable error identifier
- `message` — Human-readable error message

**Usage Example:**
```java
throw new AuthenticationException("USERNAME_EXISTS", "Username already exists");
```

#### `ValidationException`
**Package:** `com.school.identity.exception`

Thrown for validation failures:
- Missing required fields
- Invalid field format
- Password too weak

**Properties:**
- `errorCode` — Machine-readable error identifier
- `message` — Human-readable error message

**Usage Example:**
```java
throw new ValidationException("PASSWORD_WEAK", 
    "Password must contain uppercase, lowercase, digit, and special character");
```

---

## Dependencies Injected

### `UserRepository`
Spring Data JPA repository for User entity operations

**Methods Used:**
- `existsByUsername(String username)`
- `existsByEmail(String email)`
- `findByUsername(String username)`
- `findByEmail(String email)`
- `findByUsernameAndIsDeletedFalse(String username)`
- `findByEmailAndIsDeletedFalse(String email)`
- `findById(UUID userId)`
- `save(User user)`

### `PasswordEncoder`
Spring Security's BCryptPasswordEncoder

**Methods Used:**
- `encode(String rawPassword)` — Hash plaintext password
- `matches(String rawPassword, String encodedPassword)` — Verify password

---

## Database Interaction

### User Table Queries

**Sign Up:**
```sql
-- Check username exists
SELECT COUNT(*) FROM users WHERE username = ?

-- Check email exists
SELECT COUNT(*) FROM users WHERE email = ?

-- Insert new user
INSERT INTO users (id, username, email, first_name, last_name, phone, password_hash, status, is_super_admin, is_deleted, created_at, updated_at, inserted_at)
VALUES (UUID(), ?, ?, ?, ?, ?, <bcrypt_hash>, 'ACTIVE', false, false, NOW(), NOW(), NOW())
```

**Sign In:**
```sql
-- Find user by username
SELECT * FROM users WHERE username = ?

-- Find user by email
SELECT * FROM users WHERE email = ?
```

### Soft Delete Support

The service respects soft delete logic:
- `is_deleted = true` users are treated as non-existent
- Queries check `is_deleted = false` where applicable
- No permanent deletion occurs in this service

---

## API Contract Compliance

### SignUp
- **Endpoint:** POST `/api/v1/auth/signup`
- **Request:** SignUpRequest DTO (validated)
- **Response:** 201 Created, User entity mapped to UserResponse
- **Errors:** 400 VALIDATION_ERROR, 409 USERNAME_EXISTS, 409 EMAIL_EXISTS

### SignIn
- **Endpoint:** POST `/api/v1/auth/signin`
- **Request:** SignInRequest DTO (validated)
- **Response:** 200 OK, User entity mapped to SignInResponse + JWT token (JWT generation in separate step)
- **Errors:** 400 VALIDATION_ERROR, 401 INVALID_CREDENTIALS, 403 ACCOUNT_INACTIVE, 403 ACCOUNT_BLOCKED

---

## Security Considerations

### Password Security
✅ Passwords are hashed using BCrypt (industry standard)
✅ High cost factor (12) slows brute-force attacks
✅ Each password gets unique salt
✅ Original password never stored or logged

### Credential Validation
✅ Username/email checked before authenticating user
✅ Generic "INVALID_CREDENTIALS" error prevents user enumeration
✅ Soft-deleted users cannot authenticate
✅ User status (ACTIVE/INACTIVE/BLOCKED) enforced

### Best Practices
✅ No plaintext password in logs
✅ No password passed to other methods
✅ Validation exceptions provide specific errors
✅ Authentication exceptions provide generic errors (security)

---

## Testing Recommendations

### Unit Tests

#### Sign Up Tests
- ✅ Valid user registration
- ✅ Duplicate username
- ✅ Duplicate email
- ✅ Weak password
- ✅ Missing required fields

#### Sign In Tests
- ✅ Valid credentials (username)
- ✅ Valid credentials (email)
- ✅ Invalid username
- ✅ Invalid password
- ✅ Inactive account
- ✅ Blocked account
- ✅ Soft-deleted user
- ✅ Missing fields

#### Password Hashing Tests
- ✅ Password hashing produces different hashes for same plaintext
- ✅ Password verification matches hashed passwords
- ✅ Password verification rejects incorrect passwords

### Integration Tests
- ✅ End-to-end sign up with database
- ✅ End-to-end sign in with database
- ✅ User retrieval methods

---

## Future Enhancements (Not Yet Implemented)

- Account lockout after N failed login attempts
- Email verification for new sign-ups
- Password reset token generation
- JWT token generation and validation
- Login audit logging
- Two-factor authentication (2FA)
- OAuth2/OIDC integration

---

## Code Quality

✅ Constructor injection only  
✅ No field injection  
✅ No Lombok @Data  
✅ Full validation annotations in DTOs  
✅ Comprehensive JavaDoc comments  
✅ Custom exception types  
✅ Single responsibility principle  
✅ Testable design  
✅ No cross-service calls  
✅ Stateless business logic  

