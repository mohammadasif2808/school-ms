# JWT Token Management Documentation

This directory contains all documentation related to JWT (JSON Web Token) implementation in the Identity Service.

## 📄 Available Documents

| Document | Description |
|----------|-------------|
| [JWT Index](JWT_INDEX.md) | Overview and navigation for JWT features |
| [JWT Architecture](JWT_ARCHITECTURE.md) | JWT architecture and design decisions |
| [JWT Implementation](JWT_IMPLEMENTATION.md) | Detailed implementation guide |
| [JWT Quick Reference](JWT_QUICK_REFERENCE.md) | Quick reference for JWT usage |
| [JWT Completion Summary](JWT_COMPLETION_SUMMARY.md) | Implementation status |

## 🔑 JWT Features

### Token Generation
- **Library:** io.jsonwebtoken (jjwt) v0.11.5
- **Algorithm:** HS512 (HMAC-SHA512)
- **Expiration:** Configurable (default: 24 hours)
- **Refresh Token:** Supported (default: 7 days)

### Token Claims
Standard claims included in every JWT:
```json
{
  "userId": "uuid",
  "username": "string",
  "role": "ADMIN | TEACHER | STUDENT | ...",
  "permissions": ["PERMISSION_1", "PERMISSION_2"],
  "tenantId": "school-id",
  "iat": "issued-at-timestamp",
  "exp": "expiry-timestamp"
}
```

### Security Features
- Secret key based signing
- Token expiration validation
- Signature verification
- Bearer token support
- Token refresh capability

## 🔧 Key Components

- **JwtTokenProvider** - Token generation and validation
- **JwtAuthenticationFilter** - Request filter for JWT extraction
- **JwtProperties** - Configuration properties
- **JwtClaims** - Claims DTO

## 🔗 Related Documentation

- [Authentication](../authentication/) - How JWT is used in auth flow
- [Security Architecture](../../architecture/) - Security configuration
- [Controller Implementation](../../implementation/) - API endpoints using JWT

## 🚀 Quick Start

1. Review [JWT_ARCHITECTURE.md](JWT_ARCHITECTURE.md) for design
2. Check [JWT_IMPLEMENTATION.md](JWT_IMPLEMENTATION.md) for code details
3. Use [JWT_QUICK_REFERENCE.md](JWT_QUICK_REFERENCE.md) for quick lookups

---

[← Back to Documentation Index](../../INDEX.md)

