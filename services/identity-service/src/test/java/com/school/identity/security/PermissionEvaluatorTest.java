package com.school.identity.security;

import com.school.identity.domain.Permission;
import com.school.identity.domain.Role;
import com.school.identity.domain.User;
import com.school.identity.testutil.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for PermissionEvaluator
 *
 * Tests RBAC permission checking logic
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionEvaluator Tests")
class PermissionEvaluatorTest {

    private PermissionEvaluator permissionEvaluator;

    @BeforeEach
    void setUp() {
        permissionEvaluator = new PermissionEvaluator();
    }

    // ============ hasPermission TESTS ============

    @Nested
    @DisplayName("hasPermission Tests")
    class HasPermissionTests {

        @Test
        @DisplayName("GIVEN user with required permission WHEN hasPermission THEN returns true")
        void hasPermission_givenUserWithPermission_shouldReturnTrue() {
            // GIVEN
            User user = createUserWithPermissions("STUDENT_VIEW", "ATTENDANCE_MARK");
            Authentication auth = createAuthentication(user, List.of("STUDENT_VIEW", "ATTENDANCE_MARK"));

            // WHEN
            boolean result = permissionEvaluator.hasPermission(auth, "STUDENT_VIEW");

            // THEN
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("GIVEN user without required permission WHEN hasPermission THEN returns false")
        void hasPermission_givenUserWithoutPermission_shouldReturnFalse() {
            // GIVEN
            User user = createUserWithPermissions("STUDENT_VIEW");
            Authentication auth = createAuthentication(user, List.of("STUDENT_VIEW"));

            // WHEN
            boolean result = permissionEvaluator.hasPermission(auth, "ADMIN_MANAGE");

            // THEN
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("GIVEN super admin WHEN hasPermission THEN returns true regardless of permissions")
        void hasPermission_givenSuperAdmin_shouldReturnTrue() {
            // GIVEN
            User superAdmin = TestDataFactory.createSuperAdmin();
            // Super admin has no explicit permissions, but should bypass
            Authentication auth = createAuthentication(superAdmin, List.of());

            // WHEN
            boolean result = permissionEvaluator.hasPermission(auth, "ANY_PERMISSION");

            // THEN
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("GIVEN null authentication WHEN hasPermission THEN returns false")
        void hasPermission_givenNullAuthentication_shouldReturnFalse() {
            // WHEN
            boolean result = permissionEvaluator.hasPermission(null, "STUDENT_VIEW");

            // THEN
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("GIVEN unauthenticated user WHEN hasPermission THEN returns false")
        void hasPermission_givenUnauthenticated_shouldReturnFalse() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            Authentication auth = new UsernamePasswordAuthenticationToken(
                user, null, Collections.emptyList());
            // Set as not authenticated
            ((UsernamePasswordAuthenticationToken) auth).setAuthenticated(false);

            // WHEN
            boolean result = permissionEvaluator.hasPermission(auth, "STUDENT_VIEW");

            // THEN
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("GIVEN user with empty permissions WHEN hasPermission THEN returns false")
        void hasPermission_givenEmptyPermissions_shouldReturnFalse() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            Authentication auth = createAuthentication(user, List.of());

            // WHEN
            boolean result = permissionEvaluator.hasPermission(auth, "STUDENT_VIEW");

            // THEN
            assertThat(result).isFalse();
        }
    }

    // ============ hasAnyPermission TESTS ============

    @Nested
    @DisplayName("hasAnyPermission Tests")
    class HasAnyPermissionTests {

        @Test
        @DisplayName("GIVEN user with one matching permission WHEN hasAnyPermission THEN returns true")
        void hasAnyPermission_givenOneMatchingPermission_shouldReturnTrue() {
            // GIVEN
            User user = createUserWithPermissions("STUDENT_VIEW");
            Authentication auth = createAuthentication(user, List.of("STUDENT_VIEW"));

            // WHEN
            boolean result = permissionEvaluator.hasAnyPermission(
                auth, "STUDENT_VIEW", "ADMIN_MANAGE");

            // THEN
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("GIVEN user with all matching permissions WHEN hasAnyPermission THEN returns true")
        void hasAnyPermission_givenAllMatchingPermissions_shouldReturnTrue() {
            // GIVEN
            User user = createUserWithPermissions("STUDENT_VIEW", "ADMIN_MANAGE");
            Authentication auth = createAuthentication(user, List.of("STUDENT_VIEW", "ADMIN_MANAGE"));

            // WHEN
            boolean result = permissionEvaluator.hasAnyPermission(
                auth, "STUDENT_VIEW", "ADMIN_MANAGE");

            // THEN
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("GIVEN user with no matching permissions WHEN hasAnyPermission THEN returns false")
        void hasAnyPermission_givenNoMatchingPermissions_shouldReturnFalse() {
            // GIVEN
            User user = createUserWithPermissions("OTHER_PERMISSION");
            Authentication auth = createAuthentication(user, List.of("OTHER_PERMISSION"));

            // WHEN
            boolean result = permissionEvaluator.hasAnyPermission(
                auth, "STUDENT_VIEW", "ADMIN_MANAGE");

            // THEN
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("GIVEN super admin WHEN hasAnyPermission THEN returns true")
        void hasAnyPermission_givenSuperAdmin_shouldReturnTrue() {
            // GIVEN
            User superAdmin = TestDataFactory.createSuperAdmin();
            Authentication auth = createAuthentication(superAdmin, List.of());

            // WHEN
            boolean result = permissionEvaluator.hasAnyPermission(
                auth, "ANY_PERMISSION_1", "ANY_PERMISSION_2");

            // THEN
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("GIVEN null authentication WHEN hasAnyPermission THEN returns false")
        void hasAnyPermission_givenNullAuthentication_shouldReturnFalse() {
            // WHEN
            boolean result = permissionEvaluator.hasAnyPermission(
                null, "STUDENT_VIEW", "ADMIN_MANAGE");

            // THEN
            assertThat(result).isFalse();
        }
    }

    // ============ hasAllPermissions TESTS ============

    @Nested
    @DisplayName("hasAllPermissions Tests")
    class HasAllPermissionsTests {

        @Test
        @DisplayName("GIVEN user with all required permissions WHEN hasAllPermissions THEN returns true")
        void hasAllPermissions_givenAllRequired_shouldReturnTrue() {
            // GIVEN
            User user = createUserWithPermissions("STUDENT_VIEW", "STUDENT_EDIT");
            Authentication auth = createAuthentication(user, List.of("STUDENT_VIEW", "STUDENT_EDIT"));

            // WHEN
            boolean result = permissionEvaluator.hasAllPermissions(
                auth, "STUDENT_VIEW", "STUDENT_EDIT");

            // THEN
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("GIVEN user missing one permission WHEN hasAllPermissions THEN returns false")
        void hasAllPermissions_givenOneMissing_shouldReturnFalse() {
            // GIVEN
            User user = createUserWithPermissions("STUDENT_VIEW");
            Authentication auth = createAuthentication(user, List.of("STUDENT_VIEW"));

            // WHEN
            boolean result = permissionEvaluator.hasAllPermissions(
                auth, "STUDENT_VIEW", "STUDENT_EDIT");

            // THEN
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("GIVEN user with extra permissions WHEN hasAllPermissions THEN returns true")
        void hasAllPermissions_givenExtraPermissions_shouldReturnTrue() {
            // GIVEN
            User user = createUserWithPermissions("STUDENT_VIEW", "STUDENT_EDIT", "OTHER");
            Authentication auth = createAuthentication(user, List.of("STUDENT_VIEW", "STUDENT_EDIT", "OTHER"));

            // WHEN
            boolean result = permissionEvaluator.hasAllPermissions(
                auth, "STUDENT_VIEW", "STUDENT_EDIT");

            // THEN
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("GIVEN super admin WHEN hasAllPermissions THEN returns true")
        void hasAllPermissions_givenSuperAdmin_shouldReturnTrue() {
            // GIVEN
            User superAdmin = TestDataFactory.createSuperAdmin();
            Authentication auth = createAuthentication(superAdmin, List.of());

            // WHEN
            boolean result = permissionEvaluator.hasAllPermissions(
                auth, "PERMISSION_1", "PERMISSION_2", "PERMISSION_3");

            // THEN
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("GIVEN null authentication WHEN hasAllPermissions THEN returns false")
        void hasAllPermissions_givenNullAuthentication_shouldReturnFalse() {
            // WHEN
            boolean result = permissionEvaluator.hasAllPermissions(
                null, "STUDENT_VIEW", "STUDENT_EDIT");

            // THEN
            assertThat(result).isFalse();
        }
    }

    // ============ hasRole TESTS ============

    @Nested
    @DisplayName("hasRole Tests")
    class HasRoleTests {

        @Test
        @DisplayName("GIVEN user with matching role WHEN hasRole THEN returns true")
        void hasRole_givenMatchingRole_shouldReturnTrue() {
            // GIVEN
            Role teacherRole = TestDataFactory.createRoleWithName("TEACHER");
            User user = TestDataFactory.createUserWithRole(teacherRole);
            Authentication auth = createAuthentication(user, List.of());

            // WHEN
            boolean result = permissionEvaluator.hasRole(auth, "TEACHER");

            // THEN
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("GIVEN user without matching role WHEN hasRole THEN returns false")
        void hasRole_givenNonMatchingRole_shouldReturnFalse() {
            // GIVEN
            Role teacherRole = TestDataFactory.createRoleWithName("TEACHER");
            User user = TestDataFactory.createUserWithRole(teacherRole);
            Authentication auth = createAuthentication(user, List.of());

            // WHEN
            boolean result = permissionEvaluator.hasRole(auth, "ADMIN");

            // THEN
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("GIVEN super admin WHEN hasRole THEN returns true")
        void hasRole_givenSuperAdmin_shouldReturnTrue() {
            // GIVEN
            User superAdmin = TestDataFactory.createSuperAdmin();
            Authentication auth = createAuthentication(superAdmin, List.of());

            // WHEN
            boolean result = permissionEvaluator.hasRole(auth, "ANY_ROLE");

            // THEN
            assertThat(result).isTrue();
        }
    }

    // ============ isSuperAdmin TESTS ============

    @Nested
    @DisplayName("isSuperAdmin Tests")
    class IsSuperAdminTests {

        @Test
        @DisplayName("GIVEN super admin user WHEN isSuperAdmin THEN returns true")
        void isSuperAdmin_givenSuperAdmin_shouldReturnTrue() {
            // GIVEN
            User superAdmin = TestDataFactory.createSuperAdmin();
            Authentication auth = createAuthentication(superAdmin, List.of());

            // WHEN
            boolean result = permissionEvaluator.isSuperAdmin(auth);

            // THEN
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("GIVEN regular user WHEN isSuperAdmin THEN returns false")
        void isSuperAdmin_givenRegularUser_shouldReturnFalse() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            Authentication auth = createAuthentication(user, List.of());

            // WHEN
            boolean result = permissionEvaluator.isSuperAdmin(auth);

            // THEN
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("GIVEN null authentication WHEN isSuperAdmin THEN returns false")
        void isSuperAdmin_givenNullAuthentication_shouldReturnFalse() {
            // WHEN
            boolean result = permissionEvaluator.isSuperAdmin(null);

            // THEN
            assertThat(result).isFalse();
        }
    }

    // ============ SECURITY EDGE CASES ============

    @Nested
    @DisplayName("Security Edge Cases")
    class SecurityEdgeCases {

        @Test
        @DisplayName("GIVEN case-sensitive permission WHEN checking different case THEN returns false")
        void hasPermission_givenDifferentCase_shouldReturnFalse() {
            // GIVEN - Permissions are case-sensitive
            User user = createUserWithPermissions("STUDENT_VIEW");
            Authentication auth = createAuthentication(user, List.of("STUDENT_VIEW"));

            // WHEN
            boolean result = permissionEvaluator.hasPermission(auth, "student_view");

            // THEN - Should be case-sensitive (false because lowercase doesn't match)
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("GIVEN empty permission string WHEN hasPermission THEN returns false")
        void hasPermission_givenEmptyPermissionString_shouldReturnFalse() {
            // GIVEN
            User user = createUserWithPermissions("STUDENT_VIEW");
            Authentication auth = createAuthentication(user, List.of("STUDENT_VIEW"));

            // WHEN
            boolean result = permissionEvaluator.hasPermission(auth, "");

            // THEN
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("GIVEN user with null principal WHEN hasPermission THEN returns false safely")
        void hasPermission_givenNullPrincipal_shouldReturnFalseSafely() {
            // GIVEN
            Authentication auth = new UsernamePasswordAuthenticationToken(
                null, null, Collections.emptyList());

            // WHEN
            boolean result = permissionEvaluator.hasPermission(auth, "STUDENT_VIEW");

            // THEN
            assertThat(result).isFalse();
        }
    }

    // ============ HELPER METHODS ============

    private User createUserWithPermissions(String... permissionCodes) {
        Role role = TestDataFactory.createRole();
        Set<Permission> permissions = new HashSet<>();
        for (String code : permissionCodes) {
            permissions.add(TestDataFactory.createPermissionWithCode(code));
        }
        role.setPermissions(permissions);
        return TestDataFactory.createUserWithRole(role);
    }

    private Authentication createAuthentication(User user, List<String> permissions) {
        List<SimpleGrantedAuthority> authorities = permissions.stream()
            .map(SimpleGrantedAuthority::new)
            .toList();

        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }
}

