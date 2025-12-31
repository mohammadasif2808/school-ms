# Admin APIs - Quick Reference

## What Was Implemented

### 6 DTOs
- CreateRoleRequest
- CreatePermissionRequest  
- RoleResponse
- PermissionResponse
- AssignPermissionsRequest
- AssignRolesRequest

### 1 Service
- AdminService (business logic for role/permission management)

### 1 Controller
- AdminController (REST endpoints under /api/v1/admin/**)

### 1 Repository Update
- PermissionRepository (added findByModule method)

---

## API Endpoints Summary

| Method | Path | Purpose | Auth Required |
|--------|------|---------|---------------|
| POST | /api/v1/admin/roles | Create role | ROLE_MANAGE |
| GET | /api/v1/admin/roles | List roles | ROLE_VIEW |
| GET | /api/v1/admin/roles/{id} | Get role | ROLE_VIEW |
| POST | /api/v1/admin/permissions | Create permission | PERMISSION_MANAGE |
| GET | /api/v1/admin/permissions | List permissions | PERMISSION_VIEW |
| GET | /api/v1/admin/permissions/module/{module} | List by module | PERMISSION_VIEW |
| GET | /api/v1/admin/permissions/{id} | Get permission | PERMISSION_VIEW |
| POST | /api/v1/admin/roles/{roleId}/permissions | Assign permissions | ROLE_MANAGE |
| POST | /api/v1/admin/users/{userId}/roles | Assign roles | ROLE_MANAGE |

---

## Quick Usage Examples

### Create Role

```bash
curl -X POST http://localhost:8080/api/v1/admin/roles \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Teacher",
    "description": "Teacher role"
  }'
```

### List Roles

```bash
curl -X GET http://localhost:8080/api/v1/admin/roles \
  -H "Authorization: Bearer $TOKEN"
```

### Create Permission

```bash
curl -X POST http://localhost:8080/api/v1/admin/permissions \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "STUDENT_VIEW",
    "module": "STUDENT",
    "description": "View student data"
  }'
```

### Assign Permissions to Role

```bash
curl -X POST http://localhost:8080/api/v1/admin/roles/{roleId}/permissions \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "permissionIds": ["perm-id-1", "perm-id-2"]
  }'
```

### Assign Roles to User

```bash
curl -X POST http://localhost:8080/api/v1/admin/users/{userId}/roles \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "roleIds": ["role-id-1", "role-id-2"]
  }'
```

---

## Authorization Model

All endpoints use permission-based authorization:

```java
@PreAuthorize("@permissionEvaluator.hasPermission(auth, 'PERMISSION_CODE') OR " +
              "@permissionEvaluator.isSuperAdmin(auth)")
```

**Means:**
- User needs specific permission code
- OR user is super admin (bypasses check)

**Super Admin Always Allowed** regardless of assigned permissions.

---

## AdminService Methods

### Roles
- `createRole(request)` — Create role
- `getAllRoles()` — List all roles
- `getRoleById(id)` — Get role by ID

### Permissions
- `createPermission(request)` — Create permission
- `getAllPermissions()` — List all permissions
- `getPermissionsByModule(module)` — List by module
- `getPermissionById(id)` — Get permission by ID

### Assignments
- `assignPermissionsToRole(request)` — Assign perms to role
- `assignRolesToUser(request)` — Assign roles to user

---

## Error Codes

| Error | HTTP | Scenario |
|-------|------|----------|
| ROLE_EXISTS | 400 | Role name already exists |
| ROLE_NOT_FOUND | 404 | Role not found |
| PERMISSION_EXISTS | 400 | Permission code already exists |
| PERMISSION_NOT_FOUND | 400 | Permission not found |
| USER_NOT_FOUND | 404 | User not found |
| VALIDATION_ERROR | 400 | Missing/invalid fields |

---

## Transaction Safety

Both assignment operations are atomic:
- All or nothing
- If any step fails, entire transaction rolls back

```java
@Transactional
public RoleResponse assignPermissionsToRole(...)
```

---

## No Changes to Existing Code

✅ Authentication logic unchanged
✅ JWT structure unchanged
✅ Existing controllers unchanged
✅ No breaking changes

---

## Status

✅ All admin endpoints implemented
✅ Permission-based authorization
✅ DTO validation
✅ Service-layer logic
✅ Thin controller layer
✅ Transaction safety
✅ Zero compilation errors
✅ Ready for testing

---

## Next Phase

- Unit testing admin endpoints
- Integration testing with real database
- Deployment to staging
- Email notifications for role changes (optional)

