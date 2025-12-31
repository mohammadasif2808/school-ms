package com.school.identity.service;

import com.school.identity.domain.Permission;
import com.school.identity.domain.Role;
import com.school.identity.domain.RoleStatus;
import com.school.identity.domain.User;
import com.school.identity.dto.*;
import com.school.identity.exception.ValidationException;
import com.school.identity.repository.PermissionRepository;
import com.school.identity.repository.RoleRepository;
import com.school.identity.repository.UserRepository;
import com.school.identity.testutil.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AdminService
 *
 * Tests role and permission management operations
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AdminService Tests")
class AdminServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;

    // ============ CREATE ROLE TESTS ============

    @Nested
    @DisplayName("Create Role Tests")
    class CreateRoleTests {

        @Test
        @DisplayName("GIVEN valid role request WHEN createRole THEN role is created successfully")
        void createRole_givenValidRequest_shouldCreateRole() {
            // GIVEN
            CreateRoleRequest request = TestDataFactory.createCreateRoleRequest("TEACHER");

            when(roleRepository.existsByName("TEACHER")).thenReturn(false);
            when(roleRepository.save(any(Role.class))).thenAnswer(inv -> {
                Role role = inv.getArgument(0);
                role.setId(UUID.randomUUID());
                return role;
            });

            // WHEN
            RoleResponse result = adminService.createRole(request);

            // THEN
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("TEACHER");
            assertThat(result.getStatus()).isEqualTo("ACTIVE");
            verify(roleRepository).save(any(Role.class));
        }

        @Test
        @DisplayName("GIVEN role created WHEN saved THEN has correct status")
        void createRole_givenValidRequest_shouldSetActiveStatus() {
            // GIVEN
            CreateRoleRequest request = TestDataFactory.createCreateRoleRequest("ADMIN");

            when(roleRepository.existsByName("ADMIN")).thenReturn(false);
            when(roleRepository.save(any(Role.class))).thenAnswer(inv -> {
                Role role = inv.getArgument(0);
                role.setId(UUID.randomUUID());
                return role;
            });

            // WHEN
            adminService.createRole(request);

            // THEN
            ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
            verify(roleRepository).save(roleCaptor.capture());
            assertThat(roleCaptor.getValue().getStatus()).isEqualTo(RoleStatus.ACTIVE);
        }

        @Test
        @DisplayName("GIVEN duplicate role name WHEN createRole THEN throws ValidationException")
        void createRole_givenDuplicateName_shouldThrowException() {
            // GIVEN
            CreateRoleRequest request = TestDataFactory.createCreateRoleRequest("EXISTING_ROLE");
            when(roleRepository.existsByName("EXISTING_ROLE")).thenReturn(true);

            // WHEN / THEN
            assertThatThrownBy(() -> adminService.createRole(request))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "ROLE_EXISTS")
                .hasMessageContaining("already exists");

            verify(roleRepository, never()).save(any());
        }
    }

    // ============ CREATE PERMISSION TESTS ============

    @Nested
    @DisplayName("Create Permission Tests")
    class CreatePermissionTests {

        @Test
        @DisplayName("GIVEN valid permission request WHEN createPermission THEN permission is created")
        void createPermission_givenValidRequest_shouldCreatePermission() {
            // GIVEN
            CreatePermissionRequest request = TestDataFactory.createCreatePermissionRequest(
                "STUDENT_VIEW", "STUDENT");

            when(permissionRepository.findByCode("STUDENT_VIEW")).thenReturn(Optional.empty());
            when(permissionRepository.save(any(Permission.class))).thenAnswer(inv -> {
                Permission perm = inv.getArgument(0);
                perm.setId(UUID.randomUUID());
                return perm;
            });

            // WHEN
            PermissionResponse result = adminService.createPermission(request);

            // THEN
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo("STUDENT_VIEW");
            assertThat(result.getModule()).isEqualTo("STUDENT");
            verify(permissionRepository).save(any(Permission.class));
        }

        @Test
        @DisplayName("GIVEN duplicate permission code WHEN createPermission THEN throws ValidationException")
        void createPermission_givenDuplicateCode_shouldThrowException() {
            // GIVEN
            CreatePermissionRequest request = TestDataFactory.createCreatePermissionRequest(
                "EXISTING_PERM", "MODULE");
            Permission existing = TestDataFactory.createPermissionWithCode("EXISTING_PERM");

            when(permissionRepository.findByCode("EXISTING_PERM")).thenReturn(Optional.of(existing));

            // WHEN / THEN
            assertThatThrownBy(() -> adminService.createPermission(request))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "PERMISSION_EXISTS")
                .hasMessageContaining("already exists");

            verify(permissionRepository, never()).save(any());
        }
    }

    // ============ GET ROLES TESTS ============

    @Nested
    @DisplayName("Get Roles Tests")
    class GetRolesTests {

        @Test
        @DisplayName("GIVEN roles exist WHEN getAllRoles THEN returns all roles")
        void getAllRoles_givenRolesExist_shouldReturnAll() {
            // GIVEN
            Role role1 = TestDataFactory.createRoleWithName("TEACHER");
            Role role2 = TestDataFactory.createRoleWithName("ADMIN");
            when(roleRepository.findAll()).thenReturn(List.of(role1, role2));

            // WHEN
            List<RoleResponse> result = adminService.getAllRoles();

            // THEN
            assertThat(result).hasSize(2);
            assertThat(result).extracting(RoleResponse::getName)
                .containsExactlyInAnyOrder("TEACHER", "ADMIN");
        }

        @Test
        @DisplayName("GIVEN no roles WHEN getAllRoles THEN returns empty list")
        void getAllRoles_givenNoRoles_shouldReturnEmptyList() {
            // GIVEN
            when(roleRepository.findAll()).thenReturn(Collections.emptyList());

            // WHEN
            List<RoleResponse> result = adminService.getAllRoles();

            // THEN
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("GIVEN role ID WHEN getRoleById THEN returns role")
        void getRoleById_givenValidId_shouldReturnRole() {
            // GIVEN
            Role role = TestDataFactory.createRoleWithName("TEACHER");
            when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));

            // WHEN
            RoleResponse result = adminService.getRoleById(role.getId());

            // THEN
            assertThat(result.getName()).isEqualTo("TEACHER");
        }

        @Test
        @DisplayName("GIVEN invalid role ID WHEN getRoleById THEN throws ValidationException")
        void getRoleById_givenInvalidId_shouldThrowException() {
            // GIVEN
            UUID invalidId = UUID.randomUUID();
            when(roleRepository.findById(invalidId)).thenReturn(Optional.empty());

            // WHEN / THEN
            assertThatThrownBy(() -> adminService.getRoleById(invalidId))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "ROLE_NOT_FOUND");
        }
    }

    // ============ GET PERMISSIONS TESTS ============

    @Nested
    @DisplayName("Get Permissions Tests")
    class GetPermissionsTests {

        @Test
        @DisplayName("GIVEN permissions exist WHEN getAllPermissions THEN returns all")
        void getAllPermissions_givenPermissionsExist_shouldReturnAll() {
            // GIVEN
            Permission perm1 = TestDataFactory.createPermissionWithCode("STUDENT_VIEW");
            Permission perm2 = TestDataFactory.createPermissionWithCode("STUDENT_EDIT");
            when(permissionRepository.findAll()).thenReturn(List.of(perm1, perm2));

            // WHEN
            List<PermissionResponse> result = adminService.getAllPermissions();

            // THEN
            assertThat(result).hasSize(2);
            assertThat(result).extracting(PermissionResponse::getCode)
                .containsExactlyInAnyOrder("STUDENT_VIEW", "STUDENT_EDIT");
        }

        @Test
        @DisplayName("GIVEN module WHEN getPermissionsByModule THEN returns filtered permissions")
        void getPermissionsByModule_givenModule_shouldReturnFiltered() {
            // GIVEN
            Permission perm1 = TestDataFactory.createPermissionWithCodeAndModule("STUDENT_VIEW", "STUDENT");
            Permission perm2 = TestDataFactory.createPermissionWithCodeAndModule("STUDENT_EDIT", "STUDENT");
            when(permissionRepository.findByModule("STUDENT")).thenReturn(List.of(perm1, perm2));

            // WHEN
            List<PermissionResponse> result = adminService.getPermissionsByModule("STUDENT");

            // THEN
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(p -> p.getModule().equals("STUDENT"));
        }

        @Test
        @DisplayName("GIVEN permission ID WHEN getPermissionById THEN returns permission")
        void getPermissionById_givenValidId_shouldReturnPermission() {
            // GIVEN
            Permission perm = TestDataFactory.createPermissionWithCode("STUDENT_VIEW");
            when(permissionRepository.findById(perm.getId())).thenReturn(Optional.of(perm));

            // WHEN
            PermissionResponse result = adminService.getPermissionById(perm.getId());

            // THEN
            assertThat(result.getCode()).isEqualTo("STUDENT_VIEW");
        }

        @Test
        @DisplayName("GIVEN invalid permission ID WHEN getPermissionById THEN throws exception")
        void getPermissionById_givenInvalidId_shouldThrowException() {
            // GIVEN
            UUID invalidId = UUID.randomUUID();
            when(permissionRepository.findById(invalidId)).thenReturn(Optional.empty());

            // WHEN / THEN
            assertThatThrownBy(() -> adminService.getPermissionById(invalidId))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "PERMISSION_NOT_FOUND");
        }
    }

    // ============ ASSIGN PERMISSIONS TO ROLE TESTS ============

    @Nested
    @DisplayName("Assign Permissions to Role Tests")
    class AssignPermissionsTests {

        @Test
        @DisplayName("GIVEN valid role and permissions WHEN assignPermissionsToRole THEN assigns successfully")
        void assignPermissions_givenValidInput_shouldAssign() {
            // GIVEN
            Role role = TestDataFactory.createRole();
            Permission perm1 = TestDataFactory.createPermissionWithCode("STUDENT_VIEW");
            Permission perm2 = TestDataFactory.createPermissionWithCode("STUDENT_EDIT");

            AssignPermissionsRequest request = new AssignPermissionsRequest();
            request.setRoleId(role.getId().toString());
            request.setPermissionIds(Set.of(perm1.getId().toString(), perm2.getId().toString()));

            when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));
            when(permissionRepository.findAllById(anyCollection())).thenReturn(List.of(perm1, perm2));
            when(roleRepository.save(any(Role.class))).thenAnswer(inv -> inv.getArgument(0));

            // WHEN
            RoleResponse result = adminService.assignPermissionsToRole(request);

            // THEN
            assertThat(result.getPermissions()).hasSize(2);
            verify(roleRepository).save(any(Role.class));
        }

        @Test
        @DisplayName("GIVEN non-existent role WHEN assignPermissionsToRole THEN throws exception")
        void assignPermissions_givenInvalidRole_shouldThrowException() {
            // GIVEN
            UUID invalidRoleId = UUID.randomUUID();
            AssignPermissionsRequest request = new AssignPermissionsRequest();
            request.setRoleId(invalidRoleId.toString());
            request.setPermissionIds(Set.of(UUID.randomUUID().toString()));

            when(roleRepository.findById(invalidRoleId)).thenReturn(Optional.empty());

            // WHEN / THEN
            assertThatThrownBy(() -> adminService.assignPermissionsToRole(request))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "ROLE_NOT_FOUND");
        }

        @Test
        @DisplayName("GIVEN some permissions not found WHEN assignPermissionsToRole THEN throws exception")
        void assignPermissions_givenMissingPermissions_shouldThrowException() {
            // GIVEN
            Role role = TestDataFactory.createRole();
            Permission perm1 = TestDataFactory.createPermissionWithCode("STUDENT_VIEW");

            AssignPermissionsRequest request = new AssignPermissionsRequest();
            request.setRoleId(role.getId().toString());
            request.setPermissionIds(Set.of(perm1.getId().toString(), UUID.randomUUID().toString()));

            when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));
            // Only one permission found instead of two
            when(permissionRepository.findAllById(anyCollection())).thenReturn(List.of(perm1));

            // WHEN / THEN
            assertThatThrownBy(() -> adminService.assignPermissionsToRole(request))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "PERMISSION_NOT_FOUND")
                .hasMessageContaining("One or more permissions not found");
        }

        @Test
        @DisplayName("GIVEN permissions assigned WHEN replaces existing THEN old permissions removed")
        void assignPermissions_givenExistingPermissions_shouldReplace() {
            // GIVEN
            Permission oldPerm = TestDataFactory.createPermissionWithCode("OLD_PERM");
            Permission newPerm = TestDataFactory.createPermissionWithCode("NEW_PERM");

            Role role = TestDataFactory.createRole();
            role.setPermissions(new HashSet<>(Set.of(oldPerm)));

            AssignPermissionsRequest request = new AssignPermissionsRequest();
            request.setRoleId(role.getId().toString());
            request.setPermissionIds(Set.of(newPerm.getId().toString()));

            when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));
            when(permissionRepository.findAllById(anyCollection())).thenReturn(List.of(newPerm));
            when(roleRepository.save(any(Role.class))).thenAnswer(inv -> inv.getArgument(0));

            // WHEN
            RoleResponse result = adminService.assignPermissionsToRole(request);

            // THEN
            assertThat(result.getPermissions()).hasSize(1);
            assertThat(result.getPermissions())
                .extracting(PermissionResponse::getCode)
                .containsOnly("NEW_PERM");
        }
    }

    // ============ ASSIGN ROLES TO USER TESTS ============

    @Nested
    @DisplayName("Assign Roles to User Tests")
    class AssignRolesToUserTests {

        @Test
        @DisplayName("GIVEN valid user and roles WHEN assignRolesToUser THEN assigns successfully")
        void assignRoles_givenValidInput_shouldAssign() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            Role role1 = TestDataFactory.createRoleWithName("TEACHER");
            Role role2 = TestDataFactory.createRoleWithName("ADVISOR");

            AssignRolesRequest request = new AssignRolesRequest();
            request.setUserId(user.getId().toString());
            request.setRoleIds(Set.of(role1.getId().toString(), role2.getId().toString()));

            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            when(roleRepository.findAllById(anyCollection())).thenReturn(List.of(role1, role2));
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            // WHEN / THEN - Should not throw
            assertThatCode(() -> adminService.assignRolesToUser(request))
                .doesNotThrowAnyException();

            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("GIVEN non-existent user WHEN assignRolesToUser THEN throws exception")
        void assignRoles_givenInvalidUser_shouldThrowException() {
            // GIVEN
            UUID invalidUserId = UUID.randomUUID();
            AssignRolesRequest request = new AssignRolesRequest();
            request.setUserId(invalidUserId.toString());
            request.setRoleIds(Set.of(UUID.randomUUID().toString()));

            when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

            // WHEN / THEN
            assertThatThrownBy(() -> adminService.assignRolesToUser(request))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "USER_NOT_FOUND");
        }

        @Test
        @DisplayName("GIVEN deleted user WHEN assignRolesToUser THEN throws exception")
        void assignRoles_givenDeletedUser_shouldThrowException() {
            // GIVEN
            User deletedUser = TestDataFactory.createDeletedUser();
            AssignRolesRequest request = new AssignRolesRequest();
            request.setUserId(deletedUser.getId().toString());
            request.setRoleIds(Set.of(UUID.randomUUID().toString()));

            when(userRepository.findById(deletedUser.getId())).thenReturn(Optional.of(deletedUser));

            // WHEN / THEN
            assertThatThrownBy(() -> adminService.assignRolesToUser(request))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "USER_NOT_FOUND")
                .hasMessageContaining("deleted");
        }

        @Test
        @DisplayName("GIVEN some roles not found WHEN assignRolesToUser THEN throws exception")
        void assignRoles_givenMissingRoles_shouldThrowException() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            Role role1 = TestDataFactory.createRoleWithName("TEACHER");

            AssignRolesRequest request = new AssignRolesRequest();
            request.setUserId(user.getId().toString());
            request.setRoleIds(Set.of(role1.getId().toString(), UUID.randomUUID().toString()));

            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            // Only one role found instead of two
            when(roleRepository.findAllById(anyCollection())).thenReturn(List.of(role1));

            // WHEN / THEN
            assertThatThrownBy(() -> adminService.assignRolesToUser(request))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", "ROLE_NOT_FOUND")
                .hasMessageContaining("One or more roles not found");
        }
    }
}

