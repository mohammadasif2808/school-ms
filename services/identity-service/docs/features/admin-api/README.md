# Admin APIs Documentation

This directory contains all documentation related to administrative API endpoints in the Identity Service.

## 📄 Available Documents

| Document | Description |
|----------|-------------|
| [Admin APIs Implementation](ADMIN_APIS_IMPLEMENTATION.md) | Implementation details |
| [Admin APIs Integration Guide](ADMIN_APIS_INTEGRATION_GUIDE.md) | How to use admin APIs |
| [Admin APIs Quick Reference](ADMIN_APIS_QUICK_REFERENCE.md) | Quick reference for endpoints |
| [Admin APIs Completion Summary](ADMIN_APIS_COMPLETION_SUMMARY.md) | Implementation status |

## 🔧 Admin API Categories

### Role Management
Endpoints for managing roles in the system.

- **Create Role:** `POST /api/v1/admin/roles`
- **List Roles:** `GET /api/v1/admin/roles`
- **Get Role:** `GET /api/v1/admin/roles/{roleId}`
- **Update Role:** `PUT /api/v1/admin/roles/{roleId}` (future)
- **Delete Role:** `DELETE /api/v1/admin/roles/{roleId}` (future)

### Permission Management
Endpoints for managing permissions.

- **Create Permission:** `POST /api/v1/admin/permissions`
- **List Permissions:** `GET /api/v1/admin/permissions`
- **Get Permission:** `GET /api/v1/admin/permissions/{permissionId}`
- **List by Module:** `GET /api/v1/admin/permissions/module/{module}`

### Role-Permission Assignment
Endpoints for assigning permissions to roles.

- **Assign Permissions to Role:** `POST /api/v1/admin/roles/{roleId}/permissions`
- **Remove Permissions:** `DELETE /api/v1/admin/roles/{roleId}/permissions` (future)
- **List Role Permissions:** `GET /api/v1/admin/roles/{roleId}/permissions` (future)

### User-Role Assignment
Endpoints for assigning roles to users.

- **Assign Roles to User:** `POST /api/v1/admin/users/{userId}/roles`
- **Remove Roles:** `DELETE /api/v1/admin/users/{userId}/roles` (future)
- **List User Roles:** `GET /api/v1/admin/users/{userId}/roles` (future)

## 🔒 Security & Authorization

All admin endpoints require:
1. **Authentication:** Valid JWT token
2. **Authorization:** Proper permissions via `@PreAuthorize`

### Required Permissions
- **ROLE_MANAGE** - For role operations
- **PERMISSION_MANAGE** - For permission operations
- **Super Admin** - Bypass for all admin operations

Example:
```java
@PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'ROLE_MANAGE') OR " +
              "@permissionEvaluator.isSuperAdmin(authentication)")
```

## 📝 Request/Response Examples

### Create Role
```http
POST /api/v1/admin/roles
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "name": "TEACHER",
  "description": "Teaching staff with student management access"
}
```

### Assign Permissions to Role
```http
POST /api/v1/admin/roles/{roleId}/permissions
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "permissionIds": [
    "uuid-1",
    "uuid-2",
    "uuid-3"
  ]
}
```

## 🔗 Related Documentation

- [RBAC Documentation](../authorization/) - Role and permission concepts
- [Controller Implementation](../../implementation/) - AdminController details
- [Security Architecture](../../architecture/) - Authorization design

## 🚀 Quick Start

1. Review [ADMIN_APIS_IMPLEMENTATION.md](ADMIN_APIS_IMPLEMENTATION.md) for details
2. Use [ADMIN_APIS_INTEGRATION_GUIDE.md](ADMIN_APIS_INTEGRATION_GUIDE.md) for integration
3. Check [ADMIN_APIS_QUICK_REFERENCE.md](ADMIN_APIS_QUICK_REFERENCE.md) for API reference

---

[← Back to Documentation Index](../../INDEX.md)

