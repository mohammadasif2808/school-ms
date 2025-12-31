# Identity Service - Documentation Index

**Version:** 1.0.0  
**Service:** Identity Service (Authentication, Authorization, and Access Control)  
**Last Updated:** January 1, 2026

---

## 📚 Documentation Structure

This directory contains all documentation for the Identity Service, organized by category for easy navigation.

---

## 🏗️ Architecture Documentation

Core architectural decisions, security configuration, and service design.

**Location:** `docs/architecture/`

| Document | Description |
|----------|-------------|
| [Security Index](architecture/SECURITY_INDEX.md) | Overview of security architecture |
| [Security Config Implementation](architecture/SECURITY_CONFIG_IMPLEMENTATION.md) | Spring Security configuration details |
| [Security Integration Guide](architecture/SECURITY_INTEGRATION_GUIDE.md) | How to integrate with security features |
| [Security Quick Reference](architecture/SECURITY_QUICK_REFERENCE.md) | Quick security reference guide |
| [Security Completion Summary](architecture/SECURITY_COMPLETION_SUMMARY.md) | Security implementation status |
| [Skeleton Summary](architecture/SKELETON_SUMMARY.md) | Service skeleton and structure |

---

## 🔐 Features Documentation

Feature-specific documentation organized by functional area.

### Authentication
**Location:** `docs/features/authentication/`

| Document | Description |
|----------|-------------|
| [Auth Service Index](features/authentication/AUTHSERVICE_INDEX.md) | Authentication service overview |
| [Auth Service Architecture](features/authentication/AUTHSERVICE_ARCHITECTURE.md) | Authentication architecture details |
| [Auth Service Implementation](features/authentication/AUTHSERVICE_IMPLEMENTATION.md) | Implementation guide |
| [Auth Service Verification](features/authentication/AUTHSERVICE_VERIFICATION.md) | Testing and verification |
| [Auth Service Summary](features/authentication/AUTHSERVICE_SUMMARY.md) | Complete summary |

### Authorization (RBAC)
**Location:** `docs/features/authorization/`

| Document | Description |
|----------|-------------|
| [RBAC Implementation](features/authorization/RBAC_IMPLEMENTATION.md) | Role-Based Access Control implementation |
| [RBAC Integration Guide](features/authorization/RBAC_INTEGRATION_GUIDE.md) | How to integrate RBAC |
| [RBAC Quick Reference](features/authorization/RBAC_QUICK_REFERENCE.md) | Quick reference for RBAC |
| [RBAC Completion Summary](features/authorization/RBAC_COMPLETION_SUMMARY.md) | RBAC implementation status |

### JWT Token Management
**Location:** `docs/features/jwt/`

| Document | Description |
|----------|-------------|
| [JWT Index](features/jwt/JWT_INDEX.md) | JWT implementation overview |
| [JWT Architecture](features/jwt/JWT_ARCHITECTURE.md) | JWT architecture and design |
| [JWT Implementation](features/jwt/JWT_IMPLEMENTATION.md) | JWT implementation details |
| [JWT Quick Reference](features/jwt/JWT_QUICK_REFERENCE.md) | Quick reference for JWT |
| [JWT Completion Summary](features/jwt/JWT_COMPLETION_SUMMARY.md) | JWT implementation status |

### Password Reset
**Location:** `docs/features/password-reset/`

| Document | Description |
|----------|-------------|
| [Password Reset Implementation](features/password-reset/PASSWORD_RESET_IMPLEMENTATION.md) | Password reset implementation |
| [Password Reset Integration Guide](features/password-reset/PASSWORD_RESET_INTEGRATION_GUIDE.md) | Integration guide |
| [Password Reset Quick Reference](features/password-reset/PASSWORD_RESET_QUICK_REFERENCE.md) | Quick reference |
| [Password Reset Completion Summary](features/password-reset/PASSWORD_RESET_COMPLETION_SUMMARY.md) | Implementation status |

### Admin APIs
**Location:** `docs/features/admin-api/`

| Document | Description |
|----------|-------------|
| [Admin APIs Implementation](features/admin-api/ADMIN_APIS_IMPLEMENTATION.md) | Admin API implementation details |
| [Admin APIs Integration Guide](features/admin-api/ADMIN_APIS_INTEGRATION_GUIDE.md) | How to use admin APIs |
| [Admin APIs Quick Reference](features/admin-api/ADMIN_APIS_QUICK_REFERENCE.md) | Quick reference for admin endpoints |
| [Admin APIs Completion Summary](features/admin-api/ADMIN_APIS_COMPLETION_SUMMARY.md) | Admin API implementation status |

---

## 💻 Implementation Documentation

Technical implementation details for controllers, exception handling, and API documentation.

**Location:** `docs/implementation/`

| Document | Description |
|----------|-------------|
| [Controller Index](implementation/CONTROLLER_INDEX.md) | Controllers overview |
| [Controller Implementation](implementation/CONTROLLER_IMPLEMENTATION.md) | Controller implementation details |
| [Controller Integration Guide](implementation/CONTROLLER_INTEGRATION_GUIDE.md) | How to use controllers |
| [Controller Quick Reference](implementation/CONTROLLER_QUICK_REFERENCE.md) | Quick reference for endpoints |
| [Controller Completion Summary](implementation/CONTROLLER_COMPLETION_SUMMARY.md) | Controller implementation status |
| [Global Exception Handler Implementation](implementation/GLOBAL_EXCEPTION_HANDLER_IMPLEMENTATION.md) | Exception handling implementation |
| [Global Exception Handler Quick Reference](implementation/GLOBAL_EXCEPTION_HANDLER_QUICK_REFERENCE.md) | Exception handling reference |
| [Global Exception Handler Completion Summary](implementation/GLOBAL_EXCEPTION_HANDLER_COMPLETION_SUMMARY.md) | Exception handler status |
| [Swagger OpenAPI Implementation](implementation/SWAGGER_OPENAPI_IMPLEMENTATION.md) | Swagger/OpenAPI setup |
| [Swagger OpenAPI Integration Guide](implementation/SWAGGER_OPENAPI_INTEGRATION_GUIDE.md) | How to use Swagger UI |
| [Swagger OpenAPI Quick Reference](implementation/SWAGGER_OPENAPI_QUICK_REFERENCE.md) | Swagger quick reference |
| [Swagger OpenAPI Completion Summary](implementation/SWAGGER_OPENAPI_COMPLETION_SUMMARY.md) | Swagger implementation status |

---

## 📖 Quick Start

1. **New to the service?** Start with [README.md](../README.md)
2. **Understanding architecture?** Check [Architecture Documentation](#-architecture-documentation)
3. **Need to integrate?** See feature-specific integration guides
4. **API reference?** Visit [Swagger UI](http://localhost:8080/swagger-ui/index.html) when service is running
5. **Quick lookup?** Use Quick Reference guides in each section

---

## 🔗 Related Documentation

- [Project Root Documentation](../../../docs/) - Overall project documentation
- [API Contracts](../../../docs/api-contracts/identity-service.yaml) - OpenAPI specification
- [Architecture Decisions](../../../docs/decisions/) - ADRs for the project

---

## 📝 Documentation Conventions

### File Naming
- `*_INDEX.md` - Overview and navigation for a topic
- `*_ARCHITECTURE.md` - Architecture and design decisions
- `*_IMPLEMENTATION.md` - Detailed implementation guide
- `*_INTEGRATION_GUIDE.md` - How to integrate/use the feature
- `*_QUICK_REFERENCE.md` - Quick lookup reference
- `*_COMPLETION_SUMMARY.md` - Implementation status and checklist

### Document Structure
Each major feature area includes:
1. **Index/Overview** - What it is and why
2. **Architecture** - How it's designed
3. **Implementation** - How it's built
4. **Integration Guide** - How to use it
5. **Quick Reference** - Fast lookup
6. **Completion Summary** - Status tracking

---

## 🤝 Contributing to Documentation

When adding new documentation:
1. Place it in the appropriate category folder
2. Follow the naming conventions above
3. Update this INDEX.md file
4. Link related documents
5. Keep the main README.md updated

---

## 📞 Support

For questions or issues:
- Check the Quick Reference guides first
- Review Integration Guides for common use cases
- Consult Implementation docs for technical details
- See main README.md for service overview

---

**Note:** This is a living document. Keep it updated as features are added or changed.

