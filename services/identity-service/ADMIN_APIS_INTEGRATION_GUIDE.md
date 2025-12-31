# Admin APIs - Integration & Testing Guide

## Complete Request/Response Examples

### 1. Create Role

**Request:**
```http
POST /api/v1/admin/roles HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
Content-Type: application/json

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
  "description": "Teacher role with classroom management capabilities",
  "status": "ACTIVE",
  "permissions": [],
  "createdAt": "2026-01-01T10:15:30.123456",
  "updatedAt": "2026-01-01T10:15:30.123456"
}
```

**Error (409 Duplicate):**
```json
{
  "error": "ROLE_EXISTS",
  "message": "Role with name 'Teacher' already exists"
}
```

---

### 2. Create Permission

**Request:**
```http
POST /api/v1/admin/permissions HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
Content-Type: application/json

{
  "code": "STUDENT_VIEW",
  "module": "STUDENT",
  "description": "View student data and details"
}
```

**Response (201 Created):**
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "code": "STUDENT_VIEW",
  "module": "STUDENT",
  "description": "View student data and details",
  "createdAt": "2026-01-01T10:20:15.123456"
}
```

---

### 3. Assign Permissions to Role

**Request:**
```http
POST /api/v1/admin/roles/550e8400-e29b-41d4-a716-446655440000/permissions HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
Content-Type: application/json

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
  "description": "Teacher role with classroom management capabilities",
  "status": "ACTIVE",
  "permissions": [
    {
      "id": "660e8400-e29b-41d4-a716-446655440001",
      "code": "STUDENT_VIEW",
      "module": "STUDENT",
      "description": "View student data and details",
      "createdAt": "2026-01-01T10:20:15.123456"
    },
    {
      "id": "660e8400-e29b-41d4-a716-446655440002",
      "code": "STUDENT_EDIT",
      "module": "STUDENT",
      "description": "Edit student data",
      "createdAt": "2026-01-01T10:20:20.123456"
    }
  ],
  "createdAt": "2026-01-01T10:15:30.123456",
  "updatedAt": "2026-01-01T10:25:00.123456"
}
```

---

### 4. Assign Roles to User

**Request:**
```http
POST /api/v1/admin/users/550e8400-e29b-41d4-a716-446655440010/roles HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
Content-Type: application/json

{
  "roleIds": [
    "550e8400-e29b-41d4-a716-446655440000",
    "550e8400-e29b-41d4-a716-446655440005"
  ]
}
```

**Response (200 OK):**
```json
{
  "message": "Roles assigned successfully"
}
```

---

## Authorization Flow Examples

### Scenario 1: User with ROLE_MANAGE Permission

```
User: admin_user
Permissions: [ROLE_MANAGE, USER_VIEW]

Request: POST /api/v1/admin/roles
@PreAuthorize: "hasPermission(..., 'ROLE_MANAGE') OR isSuperAdmin(...)"
Check: ROLE_MANAGE in permissions? YES
Result: ALLOW (200 Created)
```

### Scenario 2: User without ROLE_MANAGE Permission

```
User: teacher_user
Permissions: [STUDENT_VIEW, ATTENDANCE_MARK]

Request: POST /api/v1/admin/roles
@PreAuthorize: "hasPermission(..., 'ROLE_MANAGE') OR isSuperAdmin(...)"
Check: ROLE_MANAGE in permissions? NO
Check: isSuperAdmin? NO
Result: DENY (403 Forbidden)
```

### Scenario 3: Super Admin (Always Allowed)

```
User: admin_super
isSuperAdmin: true
Permissions: [] (empty, doesn't matter)

Request: POST /api/v1/admin/roles
@PreAuthorize: "hasPermission(..., 'ROLE_MANAGE') OR isSuperAdmin(...)"
Check: isSuperAdmin? YES
Result: ALLOW (200 Created)
```

---

## Database Operations

### Create Role

```sql
INSERT INTO roles (id, name, description, status, created_by, created_at, updated_at)
VALUES (?, 'Teacher', '...', 'ACTIVE', 'SYSTEM', NOW(), NOW());

-- Result: 1 query
```

### Assign Permissions to Role

```sql
-- 1. Find role
SELECT * FROM roles WHERE id = ?;

-- 2. Find permissions
SELECT * FROM permissions WHERE id IN (?, ?);

-- 3. Delete existing mappings
DELETE FROM role_permissions WHERE role_id = ?;

-- 4. Insert new mappings
INSERT INTO role_permissions (role_id, permission_id) VALUES (?, ?), (?, ?);

-- 5. Update role timestamp
UPDATE roles SET updated_at = NOW() WHERE id = ?;

-- Total: 5 queries (optimized with batching)
```

### Assign Roles to User

```sql
-- 1. Find user
SELECT * FROM users WHERE id = ?;

-- 2. Find roles
SELECT * FROM roles WHERE id IN (?, ?);

-- 3. Delete existing mappings
DELETE FROM user_roles WHERE user_id = ?;

-- 4. Insert new mappings
INSERT INTO user_roles (user_id, role_id) VALUES (?, ?), (?, ?);

-- 5. Update user timestamp
UPDATE users SET last_modified_at = NOW() WHERE id = ?;

-- Total: 5 queries
```

---

## Transaction Safety

### Atomic Operation Example

```java
@Transactional
public RoleResponse assignPermissionsToRole(AssignPermissionsRequest request) {
    // Step 1: Find role
    Role role = roleRepository.findById(roleId).orElseThrow(...);
    
    // Step 2: Find permissions (could fail)
    List<Permission> permissions = permissionRepository.findAllById(permissionIds);
    
    // Step 3: Validate all found (could fail)
    if (permissions.size() != permissionIds.size()) {
        throw new ValidationException(...);  // ROLLBACK entire transaction
    }
    
    // Step 4: Update role
    role.setPermissions(new HashSet<>(permissions));
    role.setUpdatedAt(LocalDateTime.now());
    
    // Step 5: Save (could fail)
    Role savedRole = roleRepository.save(role);  // If fails, ROLLBACK
    
    return mapToRoleResponse(savedRole);
}
```

**If any step fails:**
- All database changes are rolled back
- Original state preserved
- Client receives error response

---

## Testing Strategy

### Unit Test: Create Role

```java
@Test
public void testCreateRoleSuccess() {
    // Arrange
    CreateRoleRequest request = new CreateRoleRequest("Teacher", "Teacher role");
    when(roleRepository.existsByName("Teacher")).thenReturn(false);
    when(roleRepository.save(any())).thenReturn(
        new Role() {{ setId(UUID.randomUUID()); setName("Teacher"); }}
    );
    
    // Act
    RoleResponse response = adminService.createRole(request);
    
    // Assert
    assertNotNull(response.getId());
    assertEquals("Teacher", response.getName());
    verify(roleRepository).save(any());
}

@Test
public void testCreateRoleDuplicate() {
    // Arrange
    CreateRoleRequest request = new CreateRoleRequest("Teacher", "");
    when(roleRepository.existsByName("Teacher")).thenReturn(true);
    
    // Act & Assert
    assertThrows(ValidationException.class, 
        () -> adminService.createRole(request));
}
```

### Unit Test: Assign Permissions

```java
@Test
public void testAssignPermissionsSuccess() {
    // Arrange
    UUID roleId = UUID.randomUUID();
    UUID perm1 = UUID.randomUUID();
    UUID perm2 = UUID.randomUUID();
    
    Role role = new Role() {{ setId(roleId); setName("Teacher"); }};
    Permission p1 = new Permission() {{ setId(perm1); setCode("STUDENT_VIEW"); }};
    Permission p2 = new Permission() {{ setId(perm2); setCode("STUDENT_EDIT"); }};
    
    when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
    when(permissionRepository.findAllById(any())).thenReturn(List.of(p1, p2));
    when(roleRepository.save(any())).thenReturn(role);
    
    AssignPermissionsRequest request = new AssignPermissionsRequest(
        roleId.toString(),
        Set.of(perm1.toString(), perm2.toString())
    );
    
    // Act
    RoleResponse response = adminService.assignPermissionsToRole(request);
    
    // Assert
    assertEquals(2, response.getPermissions().size());
    verify(roleRepository).save(any());
}

@Test
public void testAssignPermissionsRoleNotFound() {
    // Arrange
    UUID roleId = UUID.randomUUID();
    when(roleRepository.findById(roleId)).thenReturn(Optional.empty());
    
    AssignPermissionsRequest request = new AssignPermissionsRequest(
        roleId.toString(),
        Set.of("perm-id")
    );
    
    // Act & Assert
    assertThrows(ValidationException.class,
        () -> adminService.assignPermissionsToRole(request));
}
```

### Integration Test: End-to-End

```java
@SpringBootTest
@Transactional
public class AdminApiIntegrationTest {
    
    @Test
    public void testCreateRoleAndAssignPermissions() {
        // 1. Create permissions
        Permission perm1 = permissionRepository.save(
            new Permission() {{ 
                setCode("STUDENT_VIEW"); 
                setModule("STUDENT");
            }}
        );
        Permission perm2 = permissionRepository.save(
            new Permission() {{ 
                setCode("STUDENT_EDIT"); 
                setModule("STUDENT");
            }}
        );
        
        // 2. Create role
        CreateRoleRequest createRoleReq = new CreateRoleRequest("Teacher", "Teacher role");
        RoleResponse roleResp = adminService.createRole(createRoleReq);
        
        // 3. Assign permissions
        AssignPermissionsRequest assignReq = new AssignPermissionsRequest(
            roleResp.getId().toString(),
            Set.of(perm1.getId().toString(), perm2.getId().toString())
        );
        RoleResponse updatedRole = adminService.assignPermissionsToRole(assignReq);
        
        // 4. Verify
        assertEquals(2, updatedRole.getPermissions().size());
        assertTrue(updatedRole.getPermissions().stream()
            .map(PermissionResponse::getCode)
            .anyMatch(code -> code.equals("STUDENT_VIEW")));
    }
}
```

### Controller Test (MockMvc)

```java
@WebMvcTest(AdminController.class)
public class AdminControllerTest {
    
    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void testCreateRoleWithPermission() throws Exception {
        // Mock PermissionEvaluator to return true
        when(permissionEvaluator.hasPermission(any(), eq("ROLE_MANAGE")))
            .thenReturn(true);
        
        mockMvc.perform(post("/api/v1/admin/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Teacher",
                      "description": "Teacher role"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Teacher"));
    }
    
    @Test
    @WithMockUser(username = "user")
    public void testCreateRoleWithoutPermission() throws Exception {
        // Mock PermissionEvaluator to return false
        when(permissionEvaluator.hasPermission(any(), eq("ROLE_MANAGE")))
            .thenReturn(false);
        when(permissionEvaluator.isSuperAdmin(any())).thenReturn(false);
        
        mockMvc.perform(post("/api/v1/admin/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Teacher",
                      "description": "Teacher role"
                    }
                    """))
            .andExpect(status().isForbidden());
    }
}
```

---

## Validation Examples

### Valid Request

```json
{
  "name": "Teacher",
  "description": "Teacher role"
}
```

### Invalid Requests

**Missing name:**
```json
{
  "description": "Teacher role"
}
```
Response: 400 Bad Request - "Role name is required"

**Empty permission array:**
```json
{
  "roleId": "uuid",
  "permissionIds": []
}
```
Response: 400 Bad Request - "At least one permission ID is required"

---

## Performance Considerations

### Database Indexes

```sql
CREATE INDEX idx_role_name ON roles(name);
CREATE INDEX idx_permission_code ON permissions(code);
CREATE INDEX idx_permission_module ON permissions(module);
```

### Query Optimization

- `roleRepository.findById()` — Uses primary key (fast)
- `permissionRepository.findAllById()` — Uses IN clause (fast)
- No N+1 queries (permissions loaded eagerly)

### Caching (Optional)

```java
@Cacheable("roles")
public List<RoleResponse> getAllRoles() { ... }
```

---

## Security Notes

✅ Permission-based, not role-based authorization
✅ Super admin bypass enabled
✅ Validation on all inputs
✅ Transactions ensure consistency
✅ No sensitive data in logs
✅ Constructor injection only

---

## Status

✅ All endpoints implemented
✅ Comprehensive error handling
✅ Transaction safety
✅ Ready for testing
✅ Ready for production

