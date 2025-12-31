# Admin APIs for Role and Permission Management

## Overview

Implemented complete admin APIs for managing roles and permissions under `/api/v1/admin/**` namespace. All endpoints are protected with permission-based authorization.

---

## Architecture

```
Admin User Request
    ↓
POST /api/v1/admin/roles
    ↓
AdminController
├─ @Valid validates CreateRoleRequest
├─ @PreAuthorize checks ROLE_MANAGE permission
├─ Delegates to AdminService
└─ Returns response

AdminService
├─ Validate role name uniqueness
├─ Create Role entity
├─ Save to database
└─ Return RoleResponse

---

Similar flow for:
- Create permissions
- Assign permissions to roles
- Assign roles to users
- List roles/permissions
```

---

## Components

### 1. DTOs (Request/Response)

**CreateRoleRequest**
- `name` (String, required, unique)
- `description` (String, optional)

**CreatePermissionRequest**
- `code` (String, required, unique)
- `module` (String, required)
- `description` (String, optional)

**RoleResponse**
- `id` (UUID)
- `name` (String)
- `description` (String)
- `status` (String: ACTIVE, INACTIVE)
- `permissions` (Set<PermissionResponse>)
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)

**PermissionResponse**
- `id` (UUID)
- `code` (String)
- `module` (String)
- `description` (String)
- `createdAt` (LocalDateTime)

**AssignPermissionsRequest**
- `roleId` (String, required)
- `permissionIds` (Set<String>, required, non-empty)

**AssignRolesRequest**
- `userId` (String, required)
- `roleIds` (Set<String>, required, non-empty)

### 2. AdminService (Business Logic)

**Responsibilities:**
- Role creation and retrieval
- Permission creation and retrieval
- Assigning permissions to roles
- Assigning roles to users
- DTO mapping

**Key Methods:**

#### Role Management

```java
// Create role
RoleResponse createRole(CreateRoleRequest request)
├─ Validate role name doesn't exist
├─ Create Role entity
├─ Save to database
└─ Return RoleResponse

// List all roles
List<RoleResponse> getAllRoles()

// Get role by ID
RoleResponse getRoleById(UUID roleId)
```

#### Permission Management

```java
// Create permission
PermissionResponse createPermission(CreatePermissionRequest request)
├─ Validate permission code doesn't exist
├─ Create Permission entity
├─ Save to database
└─ Return PermissionResponse

// List all permissions
List<PermissionResponse> getAllPermissions()

// List by module
List<PermissionResponse> getPermissionsByModule(String module)

// Get permission by ID
PermissionResponse getPermissionById(UUID permissionId)
```

#### Assignment

```java
// Assign permissions to role
RoleResponse assignPermissionsToRole(AssignPermissionsRequest request)
├─ Find role by ID
├─ Find all permissions by IDs
├─ Replace role's permissions
└─ Return updated RoleResponse

// Assign roles to user
void assignRolesToUser(AssignRolesRequest request)
├─ Find user by ID
├─ Find all roles by IDs
├─ Replace user's roles
└─ Save user
```

### 3. AdminController (REST API)

**Thin controller layer:**
- Request validation via @Valid
- Authorization via @PreAuthorize
- Delegates all logic to AdminService
- Returns appropriate HTTP status codes
- Handles exceptions

### 4. Repository Updates

**PermissionRepository** (updated)
- Added `findByModule(String module)` method
- Existing methods: `findByCode()`, `existsByCode()`

---

## API Endpoints

### ROLE ENDPOINTS

#### 1. Create Role

**Endpoint:**
```
POST /api/v1/admin/roles
```

**Authentication:** Required (JWT token)
**Authorization:** Requires `ROLE_MANAGE` permission OR super admin

**Request:**
```json
{
  "name": "Teacher",
  "description": "Teacher role with classroom management capabilities"
}
```

**Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Teacher",
  "description": "Teacher role...",
  "status": "ACTIVE",
  "permissions": [],
  "createdAt": "2026-01-01T10:00:00",
  "updatedAt": "2026-01-01T10:00:00"
}
```

**Errors:**
- 400: Role name already exists
- 400: Validation error (missing name)
- 401: Unauthorized (no token)
- 403: Forbidden (missing ROLE_MANAGE permission)
- 500: Server error

---

#### 2. List All Roles

**Endpoint:**
```
GET /api/v1/admin/roles
```

**Authentication:** Required
**Authorization:** Requires `ROLE_VIEW` OR `ROLE_MANAGE` permission OR super admin

**Response (200 OK):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Teacher",
    "description": "Teacher role...",
    "status": "ACTIVE",
    "permissions": [
      {
        "id": "660e8400-e29b-41d4-a716-446655440001",
        "code": "STUDENT_VIEW",
        "module": "STUDENT",
        "description": "View students",
        "createdAt": "2026-01-01T10:00:00"
      }
    ],
    "createdAt": "2026-01-01T10:00:00",
    "updatedAt": "2026-01-01T10:00:00"
  }
]
```

---

#### 3. Get Role by ID

**Endpoint:**
```
GET /api/v1/admin/roles/{roleId}
```

**Authentication:** Required
**Authorization:** Requires `ROLE_VIEW` OR `ROLE_MANAGE` permission OR super admin

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Teacher",
  "description": "Teacher role...",
  "status": "ACTIVE",
  "permissions": [...],
  "createdAt": "2026-01-01T10:00:00",
  "updatedAt": "2026-01-01T10:00:00"
}
```

**Errors:**
- 404: Role not found
- 401/403: Unauthorized/Forbidden

---

### PERMISSION ENDPOINTS

#### 1. Create Permission

**Endpoint:**
```
POST /api/v1/admin/permissions
```

**Authentication:** Required
**Authorization:** Requires `PERMISSION_MANAGE` permission OR super admin

**Request:**
```json
{
  "code": "STUDENT_VIEW",
  "module": "STUDENT",
  "description": "View student data"
}
```

**Response (201 Created):**
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "code": "STUDENT_VIEW",
  "module": "STUDENT",
  "description": "View student data",
  "createdAt": "2026-01-01T10:00:00"
}
```

**Errors:**
- 400: Permission code already exists
- 400: Validation error
- 401/403: Unauthorized/Forbidden
- 500: Server error

---

#### 2. List All Permissions

**Endpoint:**
```
GET /api/v1/admin/permissions
```

**Authentication:** Required
**Authorization:** Requires `PERMISSION_VIEW` OR `PERMISSION_MANAGE` permission OR super admin

**Response (200 OK):**
```json
[
  {
    "id": "660e8400-e29b-41d4-a716-446655440001",
    "code": "STUDENT_VIEW",
    "module": "STUDENT",
    "description": "View student data",
    "createdAt": "2026-01-01T10:00:00"
  },
  {
    "id": "660e8400-e29b-41d4-a716-446655440002",
    "code": "STUDENT_CREATE",
    "module": "STUDENT",
    "description": "Create new student",
    "createdAt": "2026-01-01T10:00:00"
  }
]
```

---

#### 3. Get Permissions by Module

**Endpoint:**
```
GET /api/v1/admin/permissions/module/{module}
```

**Parameters:**
- `module` (path): Module name (e.g., "STUDENT", "ATTENDANCE")

**Authentication:** Required
**Authorization:** Requires `PERMISSION_VIEW` OR `PERMISSION_MANAGE` permission OR super admin

**Response (200 OK):**
```json
[
  {
    "id": "660e8400-e29b-41d4-a716-446655440001",
    "code": "STUDENT_VIEW",
    "module": "STUDENT",
    "description": "View student data",
    "createdAt": "2026-01-01T10:00:00"
  }
]
```

---

#### 4. Get Permission by ID

**Endpoint:**
```
GET /api/v1/admin/permissions/{permissionId}
```

**Authentication:** Required
**Authorization:** Requires `PERMISSION_VIEW` OR `PERMISSION_MANAGE` permission OR super admin

**Response (200 OK):**
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "code": "STUDENT_VIEW",
  "module": "STUDENT",
  "description": "View student data",
  "createdAt": "2026-01-01T10:00:00"
}
```

---

### ASSIGNMENT ENDPOINTS

#### 1. Assign Permissions to Role

**Endpoint:**
```
POST /api/v1/admin/roles/{roleId}/permissions
```

**Authentication:** Required
**Authorization:** Requires `ROLE_MANAGE` permission OR super admin

**Request:**
```json
{
  "permissionIds": [
    "660e8400-e29b-41d4-a716-446655440001",
    "660e8400-e29b-41d4-a716-446655440002"
  ]
}
```

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Teacher",
  "description": "Teacher role...",
  "status": "ACTIVE",
  "permissions": [
    {
      "id": "660e8400-e29b-41d4-a716-446655440001",
      "code": "STUDENT_VIEW",
      "module": "STUDENT",
      "description": "View student data",
      "createdAt": "2026-01-01T10:00:00"
    },
    {
      "id": "660e8400-e29b-41d4-a716-446655440002",
      "code": "STUDENT_CREATE",
      "module": "STUDENT",
      "description": "Create new student",
      "createdAt": "2026-01-01T10:00:00"
    }
  ],
  "createdAt": "2026-01-01T10:00:00",
  "updatedAt": "2026-01-01T10:00:00"
}
```

**Behavior:**
- Replaces all existing permissions with provided set
- All provided permissions must exist in database
- Role is updated immediately

**Errors:**
- 400: Permission IDs not found or invalid
- 404: Role not found
- 401/403: Unauthorized/Forbidden

---

#### 2. Assign Roles to User

**Endpoint:**
```
POST /api/v1/admin/users/{userId}/roles
```

**Authentication:** Required
**Authorization:** Requires `ROLE_MANAGE` permission OR super admin

**Request:**
```json
{
  "roleIds": [
    "550e8400-e29b-41d4-a716-446655440000",
    "550e8400-e29b-41d4-a716-446655440001"
  ]
}
```

**Response (200 OK):**
```json
{
  "message": "Roles assigned successfully"
}
```

**Behavior:**
- Replaces all existing roles with provided set
- All provided roles must exist in database
- User is updated immediately
- User cannot be deleted

**Errors:**
- 400: Role IDs not found or invalid
- 404: User not found
- 401/403: Unauthorized/Forbidden

---

## Authorization

All admin endpoints use permission-based authorization:

```java
@PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'ROLE_MANAGE') OR " +
              "@permissionEvaluator.isSuperAdmin(authentication)")
```

This means:
- User must have specific permission (e.g., `ROLE_MANAGE`)
- OR user must be super admin (bypass check)
- Super admin always has access regardless of permissions

### Required Permissions

| Operation | Permission |
|-----------|-----------|
| Create Role | ROLE_MANAGE |
| View Roles | ROLE_VIEW, ROLE_MANAGE |
| Assign Permissions to Role | ROLE_MANAGE |
| Create Permission | PERMISSION_MANAGE |
| View Permissions | PERMISSION_VIEW, PERMISSION_MANAGE |
| Assign Roles to User | ROLE_MANAGE |

---

## Error Handling

### HTTP Status Codes

| Status | Scenario |
|--------|----------|
| 201 | Resource created successfully |
| 200 | Request successful |
| 400 | Validation error or business logic error |
| 401 | Unauthorized (no token) |
| 403 | Forbidden (missing permission) |
| 404 | Resource not found |
| 500 | Server error |

### Error Response Format

```json
{
  "error": "ROLE_EXISTS",
  "message": "Role with name 'Teacher' already exists"
}
```

---

## Security Features

✅ **Permission-Based Authorization** — Uses permission codes, not hardcoded roles
✅ **Super Admin Bypass** — Admins can bypass permission checks
✅ **DTO Validation** — @Valid annotations on all requests
✅ **Transactional** — @Transactional ensures data consistency
✅ **Constructor Injection** — No field injection
✅ **Thin Controller** — All logic in service layer
✅ **Exception Handling** — Specific error codes for different scenarios

---

## Transaction Safety

**Role-Permission Assignment:**
```java
@Transactional
public RoleResponse assignPermissionsToRole(...) {
    // Atomic operation: all or nothing
    // If any step fails, entire transaction rolls back
}
```

**User-Role Assignment:**
```java
@Transactional
public void assignRolesToUser(...) {
    // Atomic operation: all or nothing
    // If any step fails, entire transaction rolls back
}
```

---

## Testing

### Unit Test: Create Role

```java
@Test
public void testCreateRoleSuccess() {
    CreateRoleRequest request = new CreateRoleRequest("Teacher", "Teacher role");
    RoleResponse response = adminService.createRole(request);
    
    assertNotNull(response.getId());
    assertEquals("Teacher", response.getName());
    assertEquals("ACTIVE", response.getStatus());
}

@Test
public void testCreateRoleDuplicate() {
    // Create first role
    adminService.createRole(new CreateRoleRequest("Teacher", "..."));
    
    // Try to create duplicate
    assertThrows(ValidationException.class, 
        () -> adminService.createRole(new CreateRoleRequest("Teacher", "...")));
}
```

### Unit Test: Assign Permissions

```java
@Test
public void testAssignPermissionsSuccess() {
    // Setup: create role and permissions
    Role role = createTestRole("Teacher");
    Permission perm1 = createTestPermission("STUDENT_VIEW");
    Permission perm2 = createTestPermission("STUDENT_CREATE");
    
    // Assign
    AssignPermissionsRequest request = new AssignPermissionsRequest(
        role.getId().toString(),
        Set.of(perm1.getId().toString(), perm2.getId().toString())
    );
    RoleResponse response = adminService.assignPermissionsToRole(request);
    
    // Verify
    assertEquals(2, response.getPermissions().size());
}
```

---

## Integration with Other Features

### With Authentication
- Uses existing JWT validation
- Inherits authenticated user from SecurityContext

### With RBAC
- Uses PermissionEvaluator for authorization
- Permission codes are user-definable

### With User Management
- Relies on existing User entity
- Assigns roles via user.setRoles()

---

## Status

✅ All admin endpoints implemented
✅ Permission-based authorization
✅ DTOs with validation
✅ Service-layer business logic
✅ Thin controller layer
✅ Transaction safety
✅ Error handling
✅ Zero compilation errors
✅ Ready for testing and deployment

