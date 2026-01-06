# Endpoint Analysis: GET /api/v1/admin/permissions/module/{module}

## Quick Summary

**Purpose:** Retrieve all permissions that belong to a specific module in the system.

**HTTP Method:** `GET`

**Endpoint:** `/api/v1/admin/permissions/module/{module}`

**Full URL:** `http://localhost:8080/api/v1/admin/permissions/module/{module}`

---

## Use Cases

### Use Case 1: Get All Student Management Permissions
```bash
GET /api/v1/admin/permissions/module/STUDENT
```
**Returns:** All permissions related to student management
- `STUDENT_VIEW`
- `STUDENT_CREATE`
- `STUDENT_EDIT`
- `STUDENT_DELETE`
- etc.

### Use Case 2: Get All Finance Module Permissions
```bash
GET /api/v1/admin/permissions/module/FINANCE
```
**Returns:** All permissions for finance operations
- `FINANCE_VIEW`
- `FINANCE_RECONCILIATION`
- `FINANCE_REPORT`
- etc.

### Use Case 3: Get All Assessment Permissions
```bash
GET /api/v1/admin/permissions/module/ASSESSMENT
```
**Returns:** All assessment-related permissions

### Use Case 4: Get All Attendance Permissions
```bash
GET /api/v1/admin/permissions/module/ATTENDANCE
```
**Returns:** All attendance-related permissions

---

## Authentication & Authorization

### Required Permission:
- `PERMISSION_VIEW` **OR**
- `PERMISSION_MANAGE` **OR**
- Super Admin role

### Security Check:
```java
@PreAuthorize("@permissionEvaluator.hasAnyPermission(authentication, 'PERMISSION_VIEW', 'PERMISSION_MANAGE') OR " +
              "@permissionEvaluator.isSuperAdmin(authentication)")
```

**Meaning:** Only users/roles with permission viewing/managing capabilities can access this endpoint.

---

## Request Details

### HTTP Request Format

```bash
GET /api/v1/admin/permissions/module/{module} HTTP/1.1
Host: localhost:8080
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

### Path Parameter

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `module` | String | Yes | The module name (e.g., STUDENT, FINANCE, ASSESSMENT) |

### Example Request with cURL

```bash
curl -X GET "http://localhost:8080/api/v1/admin/permissions/module/STUDENT" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json"
```

### Example Request with Postman

```
Method: GET
URL: http://localhost:8080/api/v1/admin/permissions/module/STUDENT
Headers:
  Authorization: Bearer <token>
  Content-Type: application/json
```

---

## Response Details

### Success Response (200 OK)

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "code": "STUDENT_VIEW",
    "module": "STUDENT",
    "description": "Permission to view student details",
    "createdAt": "2026-01-01T10:30:00"
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440002",
    "code": "STUDENT_CREATE",
    "module": "STUDENT",
    "description": "Permission to create new students",
    "createdAt": "2026-01-01T10:30:00"
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440003",
    "code": "STUDENT_EDIT",
    "module": "STUDENT",
    "description": "Permission to edit student information",
    "createdAt": "2026-01-01T10:30:00"
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440004",
    "code": "STUDENT_DELETE",
    "module": "STUDENT",
    "description": "Permission to delete students",
    "createdAt": "2026-01-01T10:30:00"
  }
]
```

### Response Fields

| Field | Type | Description |
|-------|------|-------------|
| `id` | UUID | Unique identifier for the permission |
| `code` | String | Permission code (e.g., STUDENT_VIEW) |
| `module` | String | Module this permission belongs to |
| `description` | String | Human-readable description of permission |
| `createdAt` | DateTime | When the permission was created |

---

## Database Schema

### Permissions Table

```sql
CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(100) UNIQUE NOT NULL,
    module VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### Sample Data

```sql
INSERT INTO permissions (code, module, description) VALUES
('STUDENT_VIEW', 'STUDENT', 'Permission to view student details'),
('STUDENT_CREATE', 'STUDENT', 'Permission to create new students'),
('STUDENT_EDIT', 'STUDENT', 'Permission to edit student information'),
('STUDENT_DELETE', 'STUDENT', 'Permission to delete students'),
('FINANCE_VIEW', 'FINANCE', 'Permission to view financial reports'),
('FINANCE_RECONCILIATION', 'FINANCE', 'Permission to reconcile accounts'),
('ASSESSMENT_VIEW', 'ASSESSMENT', 'Permission to view assessment results'),
('ASSESSMENT_CREATE', 'ASSESSMENT', 'Permission to create assessments'),
('ATTENDANCE_VIEW', 'ATTENDANCE', 'Permission to view attendance'),
('ATTENDANCE_MARK', 'ATTENDANCE', 'Permission to mark attendance');
```

---

## How It Works

### Step-by-Step Flow

```
1. Client sends GET request
   ↓
2. Spring Security validates JWT token
   ↓
3. @PreAuthorize checks if user has:
   ├─ PERMISSION_VIEW, OR
   ├─ PERMISSION_MANAGE, OR
   └─ Super Admin role
   ↓
4. If authorized:
   ├─ AdminController receives request
   ├─ Extracts module name from URL path
   ├─ Calls adminService.getPermissionsByModule(module)
   ↓
5. AdminService queries database:
   └─ SELECT * FROM permissions WHERE module = ?
   ↓
6. Repository returns matching permissions
   ↓
7. Service maps entities to response DTOs
   ↓
8. Controller returns 200 OK with permission list
   ↓
9. Response sent to client
```

### Code Flow

**Controller Layer** (`AdminController.java`, line 167):
```java
@GetMapping("/permissions/module/{module}")
public ResponseEntity<?> getPermissionsByModule(@PathVariable String module) {
    try {
        List<PermissionResponse> permissions = adminService.getPermissionsByModule(module);
        return ResponseEntity.ok(permissions);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(createErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
    }
}
```

**Service Layer** (`AdminService.java`, line 168):
```java
public List<PermissionResponse> getPermissionsByModule(String module) {
    return permissionRepository.findByModule(module).stream()
        .map(this::mapToPermissionResponse)
        .collect(Collectors.toList());
}
```

**Repository Layer** (`PermissionRepository.java`):
```java
List<Permission> findByModule(String module);
```

---

## Real-World Usage Examples

### Example 1: Frontend - Display Module Permissions

```javascript
// Get all student module permissions
async function getStudentPermissions() {
    const token = localStorage.getItem('jwt_token');
    
    const response = await fetch('/api/v1/admin/permissions/module/STUDENT', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    });
    
    const permissions = await response.json();
    return permissions;
}

// Usage
getStudentPermissions().then(perms => {
    perms.forEach(perm => {
        console.log(`${perm.code}: ${perm.description}`);
    });
});
```

### Example 2: Admin Panel - Show Module Permissions

```javascript
// Admin dashboard showing permissions for a specific module
async function loadModulePermissions(moduleName) {
    const response = await fetch(`/api/v1/admin/permissions/module/${moduleName}`, {
        headers: {
            'Authorization': `Bearer ${getToken()}`
        }
    });
    
    const permissions = await response.json();
    
    // Display in UI
    renderPermissionTable(permissions);
}

// Load FINANCE module permissions
loadModulePermissions('FINANCE');
```

### Example 3: Role Assignment - Select Module Permissions

When assigning permissions to a role, an admin would:
1. Select the module (e.g., STUDENT)
2. Call this endpoint to get all available permissions
3. User checks which permissions to assign to the role
4. Call POST `/api/v1/admin/roles/{roleId}/permissions` to save

```bash
# Step 1: Get available permissions for STUDENT module
curl -X GET "http://localhost:8080/api/v1/admin/permissions/module/STUDENT" \
  -H "Authorization: Bearer <token>"

# Response shows all STUDENT permissions

# Step 2: Select some and assign to a role
curl -X POST "http://localhost:8080/api/v1/admin/roles/550e8400.../permissions" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "roleId": "550e8400...",
    "permissionIds": ["550e8400-e29b-41d4-a716-446655440001", "550e8400-e29b-41d4-a716-446655440002"]
  }'
```

---

## Error Scenarios

### Error 1: Unauthorized Access

**Status:** 401 Unauthorized

**Condition:** User doesn't have required permissions

```json
{
  "error": "UNAUTHORIZED",
  "message": "User does not have permission to access this resource"
}
```

### Error 2: Invalid Token

**Status:** 401 Unauthorized

**Condition:** JWT token is expired or invalid

```json
{
  "error": "UNAUTHORIZED",
  "message": "Invalid or expired token"
}
```

### Error 3: Server Error

**Status:** 500 Internal Server Error

**Condition:** Unexpected error occurs

```json
{
  "error": "INTERNAL_SERVER_ERROR",
  "message": "An unexpected error occurred"
}
```

---

## Modules in the System

Based on the codebase, common modules include:

| Module | Purpose |
|--------|---------|
| `STUDENT` | Student management operations |
| `FINANCE` | Financial operations and reconciliation |
| `ASSESSMENT` | Assessment and grading operations |
| `ATTENDANCE` | Attendance tracking |
| `ADMIN` | Administrative operations |
| `PERMISSION` | Permission management |
| `ROLE` | Role management |

---

## Related Endpoints

| Endpoint | Purpose |
|----------|---------|
| `GET /api/v1/admin/permissions` | Get ALL permissions in system |
| `GET /api/v1/admin/permissions/{permissionId}` | Get single permission by ID |
| `GET /api/v1/admin/permissions/module/{module}` | **Get permissions by module** ← Current |
| `POST /api/v1/admin/roles/{roleId}/permissions` | Assign permissions to role |

---

## Common Use in Admin Panel

```
Admin Dashboard
├─ Roles Management
│  ├─ Create Role
│  ├─ Edit Role
│  └─ Assign Permissions to Role
│     └─ GET /api/v1/admin/permissions/module/{module}
│        (Load available permissions for user to select)
│
└─ Permissions Management
   ├─ View All Permissions
   ├─ View Permissions by Module ← This endpoint
   │  └─ GET /api/v1/admin/permissions/module/{module}
   └─ Create Permission
```

---

## Summary Table

| Attribute | Value |
|-----------|-------|
| **HTTP Method** | GET |
| **Endpoint** | `/api/v1/admin/permissions/module/{module}` |
| **Purpose** | Retrieve permissions filtered by module |
| **Required Auth** | PERMISSION_VIEW OR PERMISSION_MANAGE OR Super Admin |
| **Returns** | Array of PermissionResponse objects |
| **Status Code** | 200 (Success), 401 (Unauthorized), 500 (Server Error) |
| **Query Parameter** | None |
| **Path Parameter** | module (String) |
| **Request Body** | None |
| **Response Type** | JSON Array |

---

## Key Features

✅ **Filtered Results** - Returns only permissions for specified module

✅ **Secure** - Requires proper authentication and authorization

✅ **Fast** - Direct database query by module

✅ **RESTful** - Follows REST conventions

✅ **Stateless** - No session state required

✅ **Auditable** - Shows when permissions were created

---

*Analysis Date: January 6, 2026*
*Analyzed By: GitHub Copilot*

