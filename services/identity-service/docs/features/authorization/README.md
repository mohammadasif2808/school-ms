# Authorization (RBAC) Documentation

This directory contains all documentation related to Role-Based Access Control (RBAC) in the Identity Service.

## 📄 Available Documents

| Document | Description |
|----------|-------------|
| [RBAC Implementation](RBAC_IMPLEMENTATION.md) | Complete RBAC implementation guide |
| [RBAC Integration Guide](RBAC_INTEGRATION_GUIDE.md) | How to integrate and use RBAC |
| [RBAC Quick Reference](RBAC_QUICK_REFERENCE.md) | Quick lookup for roles and permissions |
| [RBAC Completion Summary](RBAC_COMPLETION_SUMMARY.md) | Implementation status and checklist |

## 🎭 Key Concepts

### Roles
Pre-defined roles in the system:
- **Admin** - System administrators
- **Teacher** - Teaching staff
- **Student** - Students
- **Guardian** - Parents/guardians
- **Accountant** - Finance personnel
- **Librarian** - Library staff

### Permissions
Fine-grained capabilities grouped by functional area:
- USER_VIEW, USER_CREATE, USER_EDIT, USER_DELETE
- STUDENT_VIEW, STUDENT_EDIT
- ATTENDANCE_MARK, ATTENDANCE_VIEW
- EXAM_CREATE, EXAM_GRADE
- FEE_COLLECT, FEE_VIEW
- And more...

### Permission Assignment
- Roles are assigned multiple permissions
- Users are assigned roles
- Users inherit permissions from their roles
- Super Admin override available

## 🔗 Related Documentation

- [Admin APIs](../admin-api/) - API endpoints for role/permission management
- [Security Architecture](../../architecture/) - Security design and @PreAuthorize usage
- [Authentication](../authentication/) - User authentication flow

## 🚀 Quick Start

1. Read [RBAC_IMPLEMENTATION.md](RBAC_IMPLEMENTATION.md) for architecture
2. Use [RBAC_INTEGRATION_GUIDE.md](RBAC_INTEGRATION_GUIDE.md) to integrate
3. Check [RBAC_QUICK_REFERENCE.md](RBAC_QUICK_REFERENCE.md) for quick lookups

---

[← Back to Documentation Index](../../INDEX.md)

