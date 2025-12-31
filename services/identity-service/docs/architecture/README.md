# Architecture Documentation

This directory contains architectural documentation for the Identity Service, including security configuration and service design.

## 📄 Available Documents

| Document | Description |
|----------|-------------|
| [Security Index](SECURITY_INDEX.md) | Security architecture overview |
| [Security Config Implementation](SECURITY_CONFIG_IMPLEMENTATION.md) | Spring Security configuration details |
| [Security Integration Guide](SECURITY_INTEGRATION_GUIDE.md) | How to integrate with security features |
| [Security Quick Reference](SECURITY_QUICK_REFERENCE.md) | Quick security reference |
| [Security Completion Summary](SECURITY_COMPLETION_SUMMARY.md) | Security implementation status |
| [Skeleton Summary](SKELETON_SUMMARY.md) | Service structure and skeleton |

## 🔒 Security Architecture

### Core Components
- **Spring Security 6.x** - Security framework
- **JWT Authentication** - Stateless token-based auth
- **RBAC + Permissions** - Role-based access control
- **Method-level Security** - `@PreAuthorize` annotations
- **BCrypt Password Encoding** - Secure password hashing

### Security Configuration
- Stateless session management (no cookies)
- JWT filter chain integration
- Public endpoints (signup, signin, password reset)
- Protected endpoints (admin APIs, user profile)
- CORS configuration
- CSRF disabled (stateless API)

### Key Classes
- `SecurityConfig` - Main security configuration
- `JwtAuthenticationFilter` - JWT token filter
- `PermissionEvaluator` - Custom permission checking
- `JwtTokenProvider` - Token generation/validation

## 🔗 Related Documentation

- [JWT Documentation](../features/jwt/) - JWT implementation details
- [Authentication](../features/authentication/) - Auth flow and endpoints
- [RBAC](../features/authorization/) - Role and permission system

## 🚀 Quick Start

1. Start with [SECURITY_INDEX.md](SECURITY_INDEX.md) for overview
2. Review [SECURITY_CONFIG_IMPLEMENTATION.md](SECURITY_CONFIG_IMPLEMENTATION.md) for configuration
3. Use [SECURITY_QUICK_REFERENCE.md](SECURITY_QUICK_REFERENCE.md) for quick lookups

---

[← Back to Documentation Index](../INDEX.md)

