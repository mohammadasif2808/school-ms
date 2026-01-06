# Password Reset Token Table - Complete Guide

## Overview

The **PasswordResetToken** table is a critical security component that enables secure, time-bound password recovery for users who forget their passwords. It implements industry best practices for password reset flows.

---

## 1. Purpose of the Table

| Purpose | Description |
|---------|-------------|
| **Secure Recovery** | Provides a secure mechanism to reset passwords without storing passwords in plain text |
| **Time-Limited Access** | Tokens expire after a configured duration (default: 24 hours) |
| **Single-Use Enforcement** | Each token can only be used once, preventing replay attacks |
| **Email Verification** | Confirms user has access to their registered email address |
| **Audit Trail** | Tracks when reset tokens were created and used |

---

## 2. Database Schema

### Table: `password_reset_tokens`

```sql
CREATE TABLE password_reset_tokens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    token VARCHAR(256) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_used BOOLEAN NOT NULL DEFAULT false,
    used_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Column Descriptions:

| Column | Type | Purpose |
|--------|------|---------|
| `id` | UUID | Unique identifier for token record |
| `user_id` | UUID (FK) | References the user requesting password reset |
| `token` | VARCHAR(256) | Cryptographically secure reset token (URL-safe Base64) |
| `expires_at` | TIMESTAMP | When token becomes invalid (24 hours after creation) |
| `is_used` | BOOLEAN | Flag indicating if token has been used (single-use) |
| `used_at` | TIMESTAMP | When token was actually used to reset password |
| `created_at` | TIMESTAMP | When reset request was initiated |

---

## 3. Data Model in Java

### Entity: PasswordResetToken.java

```java
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;           // Reference to User entity
    
    @NotBlank
    @Column(unique = true, nullable = false, length = 256)
    private String token;        // Secure reset token
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;  // Expiration time
    
    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;   // Single-use flag
    
    @Column(name = "used_at")
    private LocalDateTime usedAt;     // When it was used
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // Creation timestamp
    
    // Business logic method
    public boolean isValid() {
        // Token must not be used
        if (Boolean.TRUE.equals(isUsed)) return false;
        
        // Token must not be expired
        return LocalDateTime.now().isBefore(expiresAt);
    }
    
    public void markAsUsed() {
        this.isUsed = true;
        this.usedAt = LocalDateTime.now();
    }
}
```

---

## 4. Complete Password Reset Flow

### Step 1: User Requests Password Reset
**Endpoint:** `POST /api/v1/auth/forgot-password`

**Request:**
```json
{
    "email": "user@school.com"
}
```

**Process:**
```
1. Find user by email
2. Generate cryptographically secure token (32 bytes, Base64 encoded)
3. Set expiration to 24 hours from now
4. Save token to password_reset_tokens table
5. Send email with reset link: https://app.example.com/reset-password?token={token}
6. Return generic success message (don't reveal if email exists)
```

**Database Action:**
```sql
INSERT INTO password_reset_tokens (id, user_id, token, expires_at, is_used, created_at)
VALUES (uuid(), '550e8400-e29b-41d4-a716-446655440000', 'base64_encoded_token_here', 
        NOW() + INTERVAL 24 HOUR, false, NOW());
```

---

### Step 2: User Clicks Reset Link
User receives email with link containing the token and clicks it.

**Frontend** extracts token from URL and displays password reset form.

---

### Step 3: User Submits New Password
**Endpoint:** `POST /api/v1/auth/reset-password`

**Request:**
```json
{
    "token": "base64_encoded_token_here",
    "newPassword": "NewSecurePassword123!"
}
```

**Process:**
```
1. Find reset token by token string (must not be used)
2. Validate token is not expired (check expires_at > NOW)
3. Validate token is not used (is_used = false)
4. Validate new password strength:
   - Minimum 8 characters
   - At least one UPPERCASE letter
   - At least one lowercase letter
   - At least one digit
   - At least one special character (@$!%*?&)
5. Hash password using bcrypt
6. Update user.password_hash in users table
7. Mark token as used (is_used = true, used_at = NOW)
8. Return success message
```

**Database Actions:**
```sql
-- Check token validity
SELECT * FROM password_reset_tokens 
WHERE token = 'base64_encoded_token_here' 
AND is_used = false 
AND expires_at > NOW();

-- Update password
UPDATE users SET password_hash = 'bcrypt_hash_here' 
WHERE id = (SELECT user_id FROM password_reset_tokens 
            WHERE token = 'base64_encoded_token_here');

-- Mark token as used
UPDATE password_reset_tokens 
SET is_used = true, used_at = NOW() 
WHERE token = 'base64_encoded_token_here';
```

---

## 5. Security Features Implemented

### 5.1 Token Generation Security
```
✅ Cryptographically secure random generation (SecureRandom)
✅ 32 bytes = 256 bits of entropy (extremely difficult to guess)
✅ URL-safe Base64 encoding
✅ Unique constraint prevents token reuse
```

### 5.2 Expiration Security
```
✅ Tokens expire after 24 hours (configurable)
✅ Expired tokens cannot be used even if found
✅ System prevents accepting expired tokens
```

### 5.3 Single-Use Enforcement
```
✅ is_used flag prevents token reuse
✅ Once token is used, it becomes permanently invalid
✅ Prevents attackers from using same token multiple times
```

### 5.4 Email Verification
```
✅ Token is sent only to registered email
✅ Proves user has email access
✅ Generic error messages prevent email enumeration
```

### 5.5 Password Strength Validation
```
✅ Minimum 8 characters
✅ Uppercase + lowercase + digits + special characters
✅ Prevents weak passwords from being set
```

### 5.6 User Status Checks
```
✅ Validates user exists and not deleted
✅ Prevents reset for non-existent users
✅ Deleted users cannot reset password
```

---

## 6. Repository Methods

### PasswordResetTokenRepository.java

```java
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    
    /**
     * Find a password reset token by token string
     * Used for checking if token exists (regardless of status)
     */
    Optional<PasswordResetToken> findByToken(String token);
    
    /**
     * Find unused reset token
     * Only returns token if is_used = false
     * Enforces single-use security
     */
    Optional<PasswordResetToken> findByTokenAndIsUsedFalse(String token);
}
```

### Key Query:
```java
// This query finds a valid, unused token
tokenRepository.findByTokenAndIsUsedFalse(token)
```

Maps to SQL:
```sql
SELECT * FROM password_reset_tokens 
WHERE token = ? AND is_used = false
```

---

## 7. Configuration

### application.yml

```yaml
password-reset:
  token-expiry-hours: 24  # Default 24 hours, configurable
```

### Environment Variable:
```bash
PASSWORD_RESET_TOKEN_EXPIRY_HOURS=24
```

---

## 8. API Endpoints

### Endpoint 1: Request Password Reset

```
POST /api/v1/auth/forgot-password
Content-Type: application/json

{
    "email": "user@school.com"
}

Response (200 OK):
{
    "message": "If an account exists with this email, a password reset link has been sent"
}

Response (same 200 for non-existent email - security measure):
{
    "message": "If an account exists with this email, a password reset link has been sent"
}
```

### Endpoint 2: Reset Password

```
POST /api/v1/auth/reset-password
Content-Type: application/json

{
    "token": "base64_encoded_secure_token",
    "newPassword": "NewPassword123!"
}

Response (200 OK):
{
    "message": "Password has been reset successfully. You can now sign in with your new password"
}

Response (400 Bad Request - Invalid Token):
{
    "error": "INVALID_RESET_TOKEN",
    "message": "Invalid or expired reset token"
}

Response (400 Bad Request - Weak Password):
{
    "error": "PASSWORD_WEAK",
    "message": "Password must be at least 8 characters"
}
```

---

## 9. Complete Example: User Flow

### Scenario: User "john.doe@school.com" forgot password

#### Step 1: User requests reset
```bash
curl -X POST http://localhost:8080/api/v1/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{"email":"john.doe@school.com"}'
```

**Behind the scenes:**
```
1. PasswordResetService.requestPasswordReset() is called
2. User is found in database
3. Secure token generated: "SjhK-rQ_xZ9vM2nKl0pL-wXyZ1aB2cD3eF4gH5iJ"
4. Token saved with expires_at = NOW() + 24 hours
5. Email sent with link: "https://app.example.com/reset?token=SjhK-rQ_xZ9vM2nKl0pL-wXyZ1aB2cD3eF4gH5iJ"
6. Database state:
   - id: 550e8400-e29b-41d4-a716-446655440001
   - user_id: 550e8400-e29b-41d4-a716-446655440000
   - token: "SjhK-rQ_xZ9vM2nKl0pL-wXyZ1aB2cD3eF4gH5iJ"
   - expires_at: 2026-01-07 15:30:00
   - is_used: false
   - created_at: 2026-01-06 15:30:00
```

#### Step 2: User clicks link in email
- Email client or browser opens reset form
- Token is extracted from URL
- User sees "Enter New Password" form

#### Step 3: User submits new password
```bash
curl -X POST http://localhost:8080/api/v1/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token":"SjhK-rQ_xZ9vM2nKl0pL-wXyZ1aB2cD3eF4gH5iJ",
    "newPassword":"SecureNewPass2026!"
  }'
```

**Behind the scenes:**
```
1. Token found and validated (not expired, not used)
2. Password strength validated
3. Password hashed: bcrypt($2a$10$...hash...)
4. User password updated in users table
5. Token marked as used:
   - is_used: true
   - used_at: 2026-01-06 15:31:45
6. User can now login with new password
```

#### Step 4: User can now login
```bash
curl -X POST http://localhost:8080/api/v1/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username":"john.doe",
    "password":"SecureNewPass2026!"
  }'
```

---

## 10. Security Scenarios & Prevention

### Scenario 1: Attacker Tries to Reuse Token
```
1. User A gets reset token: "XYZ123"
2. User A resets password successfully
3. Token marked as used (is_used = true)
4. Attacker finds token "XYZ123" in email
5. Attacker tries to use same token
6. Query: findByTokenAndIsUsedFalse("XYZ123")
7. Returns: EMPTY (because is_used = true)
8. Result: ❌ BLOCKED - Token cannot be reused
```

### Scenario 2: Token Expires Before Use
```
1. Token created: 2026-01-06 15:30:00
2. Expires at: 2026-01-07 15:30:00
3. User forgets reset and tries 2026-01-08
4. System checks: expires_at > NOW()?
5. 2026-01-07 15:30:00 > 2026-01-08 00:00:00? = NO
6. Result: ❌ BLOCKED - Token has expired
```

### Scenario 3: Attacker Guesses Token
```
1. Token is 32 bytes = 256 bits of entropy
2. Possible combinations: 2^256 = very large number
3. Attempting one token per second would take:
   = 2^256 / (60 * 60 * 24 * 365.25) years
   = Longer than age of universe × 10^60
4. Result: ❌ BLOCKED - Token cannot be guessed
```

### Scenario 4: Email Enumeration Attack
```
Attack: Attacker tries to find if email exists
Request: POST /api/v1/auth/forgot-password {"email":"unknown@school.com"}
Response: {
    "message": "If an account exists with this email, a password reset link has been sent"
}
Same response for existing and non-existing emails!
Result: ❌ BLOCKED - Attacker learns nothing
```

---

## 11. Lifecycle of a Token

```
Timeline:

T0: User clicks "Forgot Password"
    ↓
T0+5sec: Reset request processed
         ├─ Token generated
         ├─ Token saved in DB (is_used = false)
         ├─ Email sent
         ↓

T0+1min: Token exists in DB, ready to use
         (expires_at = T0 + 24 hours)
         ↓

T0+2hours: User checks email, clicks link
           ├─ Token retrieved from DB
           ├─ Token validated:
           │  ├─ Exists? YES
           │  ├─ Expired? NO
           │  └─ Used? NO
           ├─ New password submitted
           ├─ Token marked as used (is_used = true)
           ├─ User password updated
           ↓

T0+24hours: Token expires
            ├─ Any new attempt rejected
            └─ User must request new reset

T0+7days: Cleanup (optional)
          ├─ Expired tokens can be archived/deleted
          └─ DB maintenance
```

---

## 12. Summary Table

| Aspect | Implementation |
|--------|-----------------|
| **Security Level** | ⭐⭐⭐⭐⭐ (Excellent) |
| **Token Length** | 32 bytes (256 bits) |
| **Expiration** | 24 hours (configurable) |
| **Single Use** | ✅ Enforced via `is_used` flag |
| **Email Verification** | ✅ Token sent to registered email |
| **Password Strength** | ✅ 8+ chars, mixed case, digit, special char |
| **Attack Resistance** | Guessing, replay, enumeration |
| **Audit Trail** | ✅ created_at, used_at tracked |
| **Database Integrity** | ✅ Foreign key to users |

---

## 13. Key Takeaways

1. **Time-Limited:** Tokens expire after 24 hours
2. **Single-Use:** Each token can only be used once
3. **Secure:** 256 bits of entropy prevents guessing
4. **Verified:** Proves user has email access
5. **Audited:** Tracks when tokens were used
6. **User-Safe:** Users must set strong passwords
7. **Secure Recovery:** Enables secure password reset without SMS/2FA overhead

---

## 14. Related Components

| Component | Location | Purpose |
|-----------|----------|---------|
| Entity | `domain/PasswordResetToken.java` | Data model |
| Repository | `repository/PasswordResetTokenRepository.java` | Data access |
| Service | `service/PasswordResetService.java` | Business logic |
| Controller | `controller/AuthenticationController.java` | HTTP endpoints |
| Config | `application.yml` | Token expiration settings |

---

*Analysis Date: January 6, 2026*
*Analyzed By: GitHub Copilot*

