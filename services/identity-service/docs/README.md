# Identity Service Documentation

Welcome to the Identity Service documentation! This directory contains comprehensive documentation for the authentication, authorization, and access control service.

---

## 🚀 Quick Start

### New to the service?
1. Read the main [README.md](../README.md) in the service root
2. Browse this [INDEX.md](INDEX.md) for complete documentation navigation
3. Check [Swagger UI](http://localhost:8080/swagger-ui/index.html) when the service is running

### Looking for something specific?
- **Authentication?** → [features/authentication/](features/authentication/)
- **RBAC & Permissions?** → [features/authorization/](features/authorization/)
- **JWT Tokens?** → [features/jwt/](features/jwt/)
- **Password Reset?** → [features/password-reset/](features/password-reset/)
- **Admin APIs?** → [features/admin-api/](features/admin-api/)
- **Security Config?** → [architecture/](architecture/)
- **API Endpoints?** → [implementation/](implementation/)

---

## 📚 Documentation Categories

### [🏗️ Architecture](architecture/)
Core architectural decisions, security configuration, and service design.
- Security architecture and configuration
- Spring Security setup
- Service structure and skeleton

### [🔐 Features](features/)
Feature-specific documentation organized by functional area.
- **[Authentication](features/authentication/)** - Sign up, sign in, sign out, password reset
- **[Authorization (RBAC)](features/authorization/)** - Roles, permissions, access control
- **[JWT](features/jwt/)** - Token generation, validation, refresh
- **[Password Reset](features/password-reset/)** - Forgot/reset password flow
- **[Admin APIs](features/admin-api/)** - Role and permission management

### [💻 Implementation](implementation/)
Technical implementation details for controllers, exception handling, and API documentation.
- Controller implementation and endpoints
- Global exception handling
- Swagger/OpenAPI documentation

---

## 📖 Documentation Index

**[→ View Complete Documentation Index](INDEX.md)**

The index provides:
- Full list of all documentation files
- Descriptions of each document
- Navigation between related documents
- Quick links to common topics

---

## 📝 Documentation Conventions

### File Naming
- `*_INDEX.md` - Overview and navigation for a topic
- `*_ARCHITECTURE.md` - Architecture and design decisions
- `*_IMPLEMENTATION.md` - Detailed implementation guide
- `*_INTEGRATION_GUIDE.md` - How to integrate/use the feature
- `*_QUICK_REFERENCE.md` - Quick lookup reference
- `*_COMPLETION_SUMMARY.md` - Implementation status and checklist
- `README.md` - Directory overview and navigation

### Directory Structure
```
docs/
├── INDEX.md                    # Master documentation index
├── README.md                   # This file
├── ORGANIZATION_SUMMARY.md     # How docs are organized
├── architecture/               # Architectural documentation
├── features/                   # Feature-specific documentation
│   ├── authentication/
│   ├── authorization/
│   ├── jwt/
│   ├── password-reset/
│   └── admin-api/
└── implementation/             # Implementation documentation
```

---

## 🎯 Common Use Cases

### I want to understand how authentication works
1. Go to [features/authentication/](features/authentication/)
2. Start with the README or AUTHSERVICE_INDEX.md
3. Review the architecture and implementation docs

### I need to integrate with the admin APIs
1. Go to [features/admin-api/](features/admin-api/)
2. Read the ADMIN_APIS_INTEGRATION_GUIDE.md
3. Use ADMIN_APIS_QUICK_REFERENCE.md for API details

### I want to understand JWT token handling
1. Go to [features/jwt/](features/jwt/)
2. Read JWT_ARCHITECTURE.md for design
3. Check JWT_IMPLEMENTATION.md for code details

### I need to configure security
1. Go to [architecture/](architecture/)
2. Read SECURITY_CONFIG_IMPLEMENTATION.md
3. Review SECURITY_INTEGRATION_GUIDE.md

### I want to see all available endpoints
1. Go to [implementation/](implementation/)
2. Check CONTROLLER_QUICK_REFERENCE.md
3. Or visit [Swagger UI](http://localhost:8080/swagger-ui/index.html)

---

## 🔗 External Resources

- [Main Project Docs](../../../docs/) - Overall project documentation
- [API Contract](../../../docs/api-contracts/identity-service.yaml) - OpenAPI specification
- [Architecture Decisions](../../../docs/decisions/) - Project ADRs
- [Service README](../README.md) - Service overview

---

## 🤝 Contributing to Documentation

### Adding New Documentation
1. Place it in the appropriate category folder
2. Follow the naming conventions above
3. Update the [INDEX.md](INDEX.md) file
4. Add cross-references to related docs
5. Update the directory README if adding a new category

### Updating Existing Documentation
1. Maintain consistency with existing style
2. Update "Last Updated" dates
3. Update cross-references if structure changes
4. Keep examples current with code

---

## 📊 Documentation Statistics

- **Total Documentation Files:** 40+ files
- **Categories:** 3 main categories (Architecture, Features, Implementation)
- **Feature Areas:** 5 feature areas documented
- **Navigation Files:** 8 README/INDEX files
- **Organization Status:** ✅ Complete

For detailed organization information, see [ORGANIZATION_SUMMARY.md](ORGANIZATION_SUMMARY.md)

---

## 📞 Need Help?

1. **Quick answers?** Check Quick Reference guides
2. **Integration help?** Read Integration Guides
3. **Understanding design?** Review Architecture docs
4. **Code details?** See Implementation docs
5. **API testing?** Use [Swagger UI](http://localhost:8080/swagger-ui/index.html)

---

**Last Updated:** January 1, 2026  
**Version:** 1.0.0  
**Status:** ✅ Fully Organized

---

[→ Browse Complete Documentation Index](INDEX.md)

