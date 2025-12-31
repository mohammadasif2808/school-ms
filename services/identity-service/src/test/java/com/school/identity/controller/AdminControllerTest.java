package com.school.identity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.identity.domain.User;
import com.school.identity.dto.*;
import com.school.identity.exception.GlobalExceptionHandler;
import com.school.identity.exception.ValidationException;
import com.school.identity.security.PermissionEvaluator;
import com.school.identity.service.AdminService;
import com.school.identity.testutil.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AdminController
 *
 * Tests admin REST endpoints using MockMvc
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AdminController Tests")
class AdminControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AdminService adminService;

    @Mock
    private PermissionEvaluator permissionEvaluator;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // For LocalDateTime serialization

        mockMvc = MockMvcBuilders
            .standaloneSetup(adminController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

        // Clear security context
        SecurityContextHolder.clearContext();
    }

    // ============ CREATE ROLE TESTS ============

    @Nested
    @DisplayName("POST /api/v1/admin/roles Tests")
    class CreateRoleEndpointTests {

        @Test
        @DisplayName("GIVEN valid role request WHEN POST /roles THEN returns 201")
        void createRole_givenValidRequest_shouldReturn201() throws Exception {
            // GIVEN
            CreateRoleRequest request = TestDataFactory.createCreateRoleRequest("TEACHER");
            RoleResponse response = createRoleResponse("TEACHER");

            when(adminService.createRole(any(CreateRoleRequest.class))).thenReturn(response);

            // WHEN / THEN
            mockMvc.perform(post("/api/v1/admin/roles")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("TEACHER"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
        }

        @Test
        @DisplayName("GIVEN duplicate role name WHEN POST /roles THEN returns 400")
        void createRole_givenDuplicateName_shouldReturn400() throws Exception {
            // GIVEN
            CreateRoleRequest request = TestDataFactory.createCreateRoleRequest("EXISTING");

            when(adminService.createRole(any(CreateRoleRequest.class)))
                .thenThrow(new ValidationException("ROLE_EXISTS", "Role with name 'EXISTING' already exists"));

            // WHEN / THEN
            mockMvc.perform(post("/api/v1/admin/roles")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ROLE_EXISTS"));
        }
    }

    // ============ LIST ROLES TESTS ============

    @Nested
    @DisplayName("GET /api/v1/admin/roles Tests")
    class ListRolesEndpointTests {

        @Test
        @DisplayName("GIVEN roles exist WHEN GET /roles THEN returns 200 with list")
        void listRoles_givenRolesExist_shouldReturn200() throws Exception {
            // GIVEN
            RoleResponse role1 = createRoleResponse("TEACHER");
            RoleResponse role2 = createRoleResponse("ADMIN");

            when(adminService.getAllRoles()).thenReturn(List.of(role1, role2));

            // WHEN / THEN
            mockMvc.perform(get("/api/v1/admin/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("GIVEN no roles WHEN GET /roles THEN returns 200 with empty list")
        void listRoles_givenNoRoles_shouldReturn200WithEmptyList() throws Exception {
            // GIVEN
            when(adminService.getAllRoles()).thenReturn(List.of());

            // WHEN / THEN
            mockMvc.perform(get("/api/v1/admin/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
        }
    }

    // ============ GET ROLE BY ID TESTS ============

    @Nested
    @DisplayName("GET /api/v1/admin/roles/{roleId} Tests")
    class GetRoleByIdEndpointTests {

        @Test
        @DisplayName("GIVEN valid role ID WHEN GET /roles/{id} THEN returns 200")
        void getRoleById_givenValidId_shouldReturn200() throws Exception {
            // GIVEN
            UUID roleId = UUID.randomUUID();
            RoleResponse response = createRoleResponse("TEACHER");

            when(adminService.getRoleById(roleId)).thenReturn(response);

            // WHEN / THEN
            mockMvc.perform(get("/api/v1/admin/roles/{roleId}", roleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TEACHER"));
        }

        @Test
        @DisplayName("GIVEN invalid role ID WHEN GET /roles/{id} THEN returns 404")
        void getRoleById_givenInvalidId_shouldReturn404() throws Exception {
            // GIVEN
            UUID roleId = UUID.randomUUID();

            when(adminService.getRoleById(roleId))
                .thenThrow(new ValidationException("ROLE_NOT_FOUND", "Role not found"));

            // WHEN / THEN
            mockMvc.perform(get("/api/v1/admin/roles/{roleId}", roleId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("ROLE_NOT_FOUND"));
        }
    }

    // ============ CREATE PERMISSION TESTS ============

    @Nested
    @DisplayName("POST /api/v1/admin/permissions Tests")
    class CreatePermissionEndpointTests {

        @Test
        @DisplayName("GIVEN valid permission request WHEN POST /permissions THEN returns 201")
        void createPermission_givenValidRequest_shouldReturn201() throws Exception {
            // GIVEN
            CreatePermissionRequest request = TestDataFactory.createCreatePermissionRequest(
                "STUDENT_VIEW", "STUDENT");
            PermissionResponse response = createPermissionResponse("STUDENT_VIEW", "STUDENT");

            when(adminService.createPermission(any(CreatePermissionRequest.class))).thenReturn(response);

            // WHEN / THEN
            mockMvc.perform(post("/api/v1/admin/permissions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("STUDENT_VIEW"))
                .andExpect(jsonPath("$.module").value("STUDENT"));
        }

        @Test
        @DisplayName("GIVEN duplicate permission code WHEN POST /permissions THEN returns 400")
        void createPermission_givenDuplicateCode_shouldReturn400() throws Exception {
            // GIVEN
            CreatePermissionRequest request = TestDataFactory.createCreatePermissionRequest(
                "EXISTING", "MODULE");

            when(adminService.createPermission(any(CreatePermissionRequest.class)))
                .thenThrow(new ValidationException("PERMISSION_EXISTS", "Permission already exists"));

            // WHEN / THEN
            mockMvc.perform(post("/api/v1/admin/permissions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("PERMISSION_EXISTS"));
        }
    }

    // ============ LIST PERMISSIONS TESTS ============

    @Nested
    @DisplayName("GET /api/v1/admin/permissions Tests")
    class ListPermissionsEndpointTests {

        @Test
        @DisplayName("GIVEN permissions exist WHEN GET /permissions THEN returns 200")
        void listPermissions_givenPermissionsExist_shouldReturn200() throws Exception {
            // GIVEN
            PermissionResponse perm1 = createPermissionResponse("STUDENT_VIEW", "STUDENT");
            PermissionResponse perm2 = createPermissionResponse("STUDENT_EDIT", "STUDENT");

            when(adminService.getAllPermissions()).thenReturn(List.of(perm1, perm2));

            // WHEN / THEN
            mockMvc.perform(get("/api/v1/admin/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("GIVEN module WHEN GET /permissions/module/{module} THEN returns filtered")
        void listPermissionsByModule_shouldReturnFiltered() throws Exception {
            // GIVEN
            PermissionResponse perm = createPermissionResponse("STUDENT_VIEW", "STUDENT");

            when(adminService.getPermissionsByModule("STUDENT")).thenReturn(List.of(perm));

            // WHEN / THEN
            mockMvc.perform(get("/api/v1/admin/permissions/module/{module}", "STUDENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].module").value("STUDENT"));
        }
    }

    // ============ ASSIGN PERMISSIONS TO ROLE TESTS ============

    @Nested
    @DisplayName("POST /api/v1/admin/roles/{roleId}/permissions Tests")
    class AssignPermissionsEndpointTests {

        @Test
        @DisplayName("GIVEN valid request WHEN POST /roles/{id}/permissions THEN returns 200")
        void assignPermissions_givenValidRequest_shouldReturn200() throws Exception {
            // GIVEN
            UUID roleId = UUID.randomUUID();
            String requestBody = """
                {
                    "roleId": "%s",
                    "permissionIds": ["%s", "%s"]
                }
                """.formatted(roleId.toString(), UUID.randomUUID(), UUID.randomUUID());

            RoleResponse response = createRoleResponse("TEACHER");
            when(adminService.assignPermissionsToRole(any(AssignPermissionsRequest.class)))
                .thenReturn(response);

            // WHEN / THEN
            mockMvc.perform(post("/api/v1/admin/roles/{roleId}/permissions", roleId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GIVEN non-existent role WHEN POST /roles/{id}/permissions THEN returns 404")
        void assignPermissions_givenInvalidRole_shouldReturn404() throws Exception {
            // GIVEN
            UUID roleId = UUID.randomUUID();
            String requestBody = """
                {
                    "roleId": "%s",
                    "permissionIds": ["%s"]
                }
                """.formatted(roleId.toString(), UUID.randomUUID());

            when(adminService.assignPermissionsToRole(any(AssignPermissionsRequest.class)))
                .thenThrow(new ValidationException("ROLE_NOT_FOUND", "Role not found"));

            // WHEN / THEN
            mockMvc.perform(post("/api/v1/admin/roles/{roleId}/permissions", roleId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("ROLE_NOT_FOUND"));
        }
    }

    // ============ ASSIGN ROLES TO USER TESTS ============

    @Nested
    @DisplayName("POST /api/v1/admin/users/{userId}/roles Tests")
    class AssignRolesToUserEndpointTests {

        @Test
        @DisplayName("GIVEN valid request WHEN POST /users/{id}/roles THEN returns 200")
        void assignRoles_givenValidRequest_shouldReturn200() throws Exception {
            // GIVEN
            UUID userId = UUID.randomUUID();
            String requestBody = """
                {
                    "userId": "%s",
                    "roleIds": ["%s"]
                }
                """.formatted(userId.toString(), UUID.randomUUID());

            doNothing().when(adminService).assignRolesToUser(any(AssignRolesRequest.class));

            // WHEN / THEN
            mockMvc.perform(post("/api/v1/admin/users/{userId}/roles", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Roles assigned successfully"));
        }

        @Test
        @DisplayName("GIVEN non-existent user WHEN POST /users/{id}/roles THEN returns 404")
        void assignRoles_givenInvalidUser_shouldReturn404() throws Exception {
            // GIVEN
            UUID userId = UUID.randomUUID();
            String requestBody = """
                {
                    "userId": "%s",
                    "roleIds": ["%s"]
                }
                """.formatted(userId.toString(), UUID.randomUUID());

            doThrow(new ValidationException("USER_NOT_FOUND", "User not found"))
                .when(adminService).assignRolesToUser(any(AssignRolesRequest.class));

            // WHEN / THEN
            mockMvc.perform(post("/api/v1/admin/users/{userId}/roles", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("USER_NOT_FOUND"));
        }
    }

    // ============ RESPONSE FORMAT TESTS ============

    @Nested
    @DisplayName("Response Format Tests")
    class ResponseFormatTests {

        @Test
        @DisplayName("GIVEN role creation WHEN success THEN response has all fields")
        void createRole_response_shouldHaveAllFields() throws Exception {
            // GIVEN
            CreateRoleRequest request = TestDataFactory.createCreateRoleRequest("NEW_ROLE");
            RoleResponse response = createRoleResponse("NEW_ROLE");

            when(adminService.createRole(any(CreateRoleRequest.class))).thenReturn(response);

            // WHEN / THEN
            mockMvc.perform(post("/api/v1/admin/roles")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.createdAt").exists());
        }

        @Test
        @DisplayName("GIVEN permission creation WHEN success THEN response has all fields")
        void createPermission_response_shouldHaveAllFields() throws Exception {
            // GIVEN
            CreatePermissionRequest request = TestDataFactory.createCreatePermissionRequest(
                "NEW_PERM", "MODULE");
            PermissionResponse response = createPermissionResponse("NEW_PERM", "MODULE");

            when(adminService.createPermission(any(CreatePermissionRequest.class))).thenReturn(response);

            // WHEN / THEN
            mockMvc.perform(post("/api/v1/admin/permissions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.module").exists())
                .andExpect(jsonPath("$.createdAt").exists());
        }

        @Test
        @DisplayName("GIVEN error WHEN response returned THEN follows standard format")
        void error_response_shouldFollowFormat() throws Exception {
            // GIVEN
            when(adminService.createRole(any(CreateRoleRequest.class)))
                .thenThrow(new ValidationException("ROLE_EXISTS", "Role already exists"));

            // WHEN / THEN
            // Controller returns {error, message} format
            mockMvc.perform(post("/api/v1/admin/roles")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"TEST\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ROLE_EXISTS"))
                .andExpect(jsonPath("$.message").value("Role already exists"));
        }
    }

    // ============ HELPER METHODS ============

    private RoleResponse createRoleResponse(String name) {
        RoleResponse response = new RoleResponse();
        response.setId(UUID.randomUUID());
        response.setName(name);
        response.setDescription("Test role");
        response.setStatus("ACTIVE");
        response.setCreatedAt(LocalDateTime.now());
        response.setPermissions(Set.of());
        return response;
    }

    private PermissionResponse createPermissionResponse(String code, String module) {
        PermissionResponse response = new PermissionResponse();
        response.setId(UUID.randomUUID());
        response.setCode(code);
        response.setModule(module);
        response.setDescription("Test permission");
        response.setCreatedAt(LocalDateTime.now());
        return response;
    }
}

