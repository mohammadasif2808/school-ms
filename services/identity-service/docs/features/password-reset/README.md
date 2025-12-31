# Password Reset Documentation

This directory contains all documentation related to password reset functionality in the Identity Service.

## 📄 Available Documents

| Document | Description |
|----------|-------------|
| [Password Reset Implementation](PASSWORD_RESET_IMPLEMENTATION.md) | Implementation guide |
| [Password Reset Integration Guide](PASSWORD_RESET_INTEGRATION_GUIDE.md) | How to integrate password reset |
| [Password Reset Quick Reference](PASSWORD_RESET_QUICK_REFERENCE.md) | Quick reference guide |
| [Password Reset Completion Summary](PASSWORD_RESET_COMPLETION_SUMMARY.md) | Implementation status |

## 🔐 Password Reset Flow

### 1. Forgot Password (Request Reset)
- **Endpoint:** `POST /api/v1/auth/forgot-password`
- **Input:** Email address
- **Process:**
  - Validates email exists
  - Generates secure reset token
  - Stores token with expiration
  - Sends email with reset link (future: email service integration)
- **Output:** Success confirmation

### 2. Reset Password (Complete Reset)
- **Endpoint:** `POST /api/v1/auth/reset-password`
- **Input:** Reset token + new password
- **Process:**
  - Validates token (exists, not expired, not used)
  - Validates new password strength
  - Updates password hash
  - Invalidates reset token
- **Output:** Success confirmation

## 🔒 Security Features

- **Secure Tokens:** Cryptographically secure random tokens
- **Expiration:** Tokens expire after configurable time (default: 1 hour)
- **One-time Use:** Tokens can only be used once
- **Password Validation:** Enforces password complexity rules
- **BCrypt Hashing:** Passwords stored with BCrypt (cost factor: 12)

## 🔧 Key Components

- **PasswordResetToken** - Entity for storing reset tokens
- **PasswordResetRepository** - Data access layer
- **AuthenticationService** - Business logic for password reset
- **AuthenticationController** - API endpoints

## 🔗 Related Documentation

- [Authentication](../authentication/) - Overall authentication system
- [Security Architecture](../../architecture/) - Security design
- [Controller Implementation](../../implementation/) - API endpoint details

## 🚀 Quick Start

1. Check [PASSWORD_RESET_IMPLEMENTATION.md](PASSWORD_RESET_IMPLEMENTATION.md) for details
2. Use [PASSWORD_RESET_INTEGRATION_GUIDE.md](PASSWORD_RESET_INTEGRATION_GUIDE.md) for integration
3. Reference [PASSWORD_RESET_QUICK_REFERENCE.md](PASSWORD_RESET_QUICK_REFERENCE.md) for API usage

---

[← Back to Documentation Index](../../INDEX.md)

