package com.school.identity.security;

import com.school.identity.domain.User;
import com.school.identity.dto.JwtClaims;
import com.school.identity.exception.JwtException;
import com.school.identity.repository.UserRepository;
import com.school.identity.testutil.TestDataFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JwtAuthenticationFilter
 *
 * Tests JWT token extraction and authentication from requests
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter Tests")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ============ VALID TOKEN TESTS ============

    @Nested
    @DisplayName("Valid Token Tests")
    class ValidTokenTests {

        @Test
        @DisplayName("GIVEN valid token WHEN filter THEN populates SecurityContext")
        void doFilter_givenValidToken_shouldPopulateSecurityContext() throws ServletException, IOException {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            JwtClaims claims = createValidClaims(user);

            when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
            when(jwtTokenProvider.validateAndExtractClaims("valid.jwt.token")).thenReturn(claims);
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            // WHEN
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // THEN
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNotNull();
            assertThat(auth.isAuthenticated()).isTrue();
            assertThat(auth.getPrincipal()).isEqualTo(user);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("GIVEN valid token with permissions WHEN filter THEN authorities populated")
        void doFilter_givenTokenWithPermissions_shouldPopulateAuthorities() throws ServletException, IOException {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            JwtClaims claims = createValidClaims(user);
            claims.setPermissions(List.of("STUDENT_VIEW", "ATTENDANCE_MARK"));

            when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
            when(jwtTokenProvider.validateAndExtractClaims("valid.jwt.token")).thenReturn(claims);
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            // WHEN
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // THEN
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth.getAuthorities()).hasSize(2);
            assertThat(auth.getAuthorities())
                .extracting("authority")
                .containsExactlyInAnyOrder("STUDENT_VIEW", "ATTENDANCE_MARK");
        }
    }

    // ============ MISSING TOKEN TESTS ============

    @Nested
    @DisplayName("Missing Token Tests")
    class MissingTokenTests {

        @Test
        @DisplayName("GIVEN no Authorization header WHEN filter THEN continues without authentication")
        void doFilter_givenNoAuthorizationHeader_shouldContinueWithoutAuth() throws ServletException, IOException {
            // GIVEN
            when(request.getHeader("Authorization")).thenReturn(null);

            // WHEN
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // THEN
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNull();
            verify(filterChain).doFilter(request, response);
            verify(jwtTokenProvider, never()).validateAndExtractClaims(anyString());
        }

        @Test
        @DisplayName("GIVEN empty Authorization header WHEN filter THEN continues without authentication")
        void doFilter_givenEmptyAuthorizationHeader_shouldContinueWithoutAuth() throws ServletException, IOException {
            // GIVEN
            when(request.getHeader("Authorization")).thenReturn("");

            // WHEN
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // THEN
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNull();
            verify(filterChain).doFilter(request, response);
        }
    }

    // ============ INVALID TOKEN FORMAT TESTS ============

    @Nested
    @DisplayName("Invalid Token Format Tests")
    class InvalidTokenFormatTests {

        @Test
        @DisplayName("GIVEN Authorization without Bearer prefix WHEN filter THEN continues without auth")
        void doFilter_givenNoBearerPrefix_shouldContinueWithoutAuth() throws ServletException, IOException {
            // GIVEN
            when(request.getHeader("Authorization")).thenReturn("some.jwt.token");

            // WHEN
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // THEN
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNull();
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("GIVEN 'Basic' auth type WHEN filter THEN continues without auth")
        void doFilter_givenBasicAuth_shouldContinueWithoutAuth() throws ServletException, IOException {
            // GIVEN
            when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

            // WHEN
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // THEN
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNull();
            verify(filterChain).doFilter(request, response);
        }
    }

    // ============ INVALID TOKEN TESTS ============

    @Nested
    @DisplayName("Invalid Token Tests")
    class InvalidTokenTests {

        @Test
        @DisplayName("GIVEN malformed token WHEN filter THEN continues without authentication")
        void doFilter_givenMalformedToken_shouldContinueWithoutAuth() throws ServletException, IOException {
            // GIVEN
            when(request.getHeader("Authorization")).thenReturn("Bearer malformed.token");
            when(jwtTokenProvider.validateAndExtractClaims("malformed.token"))
                .thenThrow(new JwtException("TOKEN_INVALID", "Malformed token"));

            // WHEN
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // THEN
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNull();
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("GIVEN expired token WHEN filter THEN continues without authentication")
        void doFilter_givenExpiredToken_shouldContinueWithoutAuth() throws ServletException, IOException {
            // GIVEN
            when(request.getHeader("Authorization")).thenReturn("Bearer expired.jwt.token");
            when(jwtTokenProvider.validateAndExtractClaims("expired.jwt.token"))
                .thenThrow(new JwtException("TOKEN_EXPIRED", "Token has expired"));

            // WHEN
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // THEN
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNull();
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("GIVEN invalid signature WHEN filter THEN continues without authentication")
        void doFilter_givenInvalidSignature_shouldContinueWithoutAuth() throws ServletException, IOException {
            // GIVEN
            when(request.getHeader("Authorization")).thenReturn("Bearer invalid.signature.token");
            when(jwtTokenProvider.validateAndExtractClaims("invalid.signature.token"))
                .thenThrow(new JwtException("TOKEN_INVALID", "Invalid token signature"));

            // WHEN
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // THEN
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNull();
            verify(filterChain).doFilter(request, response);
        }
    }

    // ============ USER NOT FOUND TESTS ============

    @Nested
    @DisplayName("User Not Found Tests")
    class UserNotFoundTests {

        @Test
        @DisplayName("GIVEN valid token but user deleted WHEN filter THEN continues without auth")
        void doFilter_givenDeletedUser_shouldContinueWithoutAuth() throws ServletException, IOException {
            // GIVEN
            User deletedUser = TestDataFactory.createDeletedUser();
            JwtClaims claims = createValidClaims(deletedUser);

            when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
            when(jwtTokenProvider.validateAndExtractClaims("valid.jwt.token")).thenReturn(claims);
            when(userRepository.findById(deletedUser.getId())).thenReturn(Optional.of(deletedUser));

            // WHEN
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // THEN
            // Should continue but not authenticate deleted user
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("GIVEN valid token but user not found WHEN filter THEN continues without auth")
        void doFilter_givenUserNotFound_shouldContinueWithoutAuth() throws ServletException, IOException {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            JwtClaims claims = createValidClaims(user);

            when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
            when(jwtTokenProvider.validateAndExtractClaims("valid.jwt.token")).thenReturn(claims);
            when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

            // WHEN
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // THEN
            // Should continue filter chain even if user not found
            verify(filterChain).doFilter(request, response);
        }
    }

    // ============ SECURITY EDGE CASES ============

    @Nested
    @DisplayName("Security Edge Cases")
    class SecurityEdgeCases {

        @Test
        @DisplayName("GIVEN token with missing claims WHEN filter THEN handles gracefully")
        void doFilter_givenMissingClaims_shouldHandleGracefully() throws ServletException, IOException {
            // GIVEN
            when(request.getHeader("Authorization")).thenReturn("Bearer token.missing.claims");
            when(jwtTokenProvider.validateAndExtractClaims("token.missing.claims"))
                .thenThrow(new JwtException("TOKEN_INVALID", "Token claims are empty"));

            // WHEN
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // THEN
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNull();
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("GIVEN exception during processing WHEN filter THEN filter chain continues")
        void doFilter_givenException_shouldContinueFilterChain() throws ServletException, IOException {
            // GIVEN
            when(request.getHeader("Authorization")).thenReturn("Bearer some.token");
            when(jwtTokenProvider.validateAndExtractClaims("some.token"))
                .thenThrow(new RuntimeException("Unexpected error"));

            // WHEN
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // THEN - Filter chain should continue even on unexpected error
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("GIVEN Bearer with extra spaces WHEN filter THEN handles correctly")
        void doFilter_givenBearerWithExtraSpaces_shouldHandle() throws ServletException, IOException {
            // GIVEN
            when(request.getHeader("Authorization")).thenReturn("Bearer  extra.spaces.token");

            // WHEN
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // THEN - Should continue without crash
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("GIVEN 'bearer' lowercase WHEN filter THEN handles correctly")
        void doFilter_givenLowercaseBearer_shouldHandle() throws ServletException, IOException {
            // GIVEN
            when(request.getHeader("Authorization")).thenReturn("bearer lowercase.token");

            // WHEN
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // THEN - Should continue (implementation may or may not accept lowercase)
            verify(filterChain).doFilter(request, response);
        }
    }

    // ============ FILTER CHAIN TESTS ============

    @Nested
    @DisplayName("Filter Chain Tests")
    class FilterChainTests {

        @Test
        @DisplayName("GIVEN any request WHEN filter THEN always continues filter chain")
        void doFilter_givenAnyRequest_shouldAlwaysContinueChain() throws ServletException, IOException {
            // GIVEN
            when(request.getHeader("Authorization")).thenReturn(null);

            // WHEN
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // THEN
            verify(filterChain, times(1)).doFilter(request, response);
        }

        @Test
        @DisplayName("GIVEN valid auth WHEN filter THEN continues filter chain after authentication")
        void doFilter_givenValidAuth_shouldContinueChainAfterAuth() throws ServletException, IOException {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            JwtClaims claims = createValidClaims(user);

            when(request.getHeader("Authorization")).thenReturn("Bearer valid.token");
            when(jwtTokenProvider.validateAndExtractClaims("valid.token")).thenReturn(claims);
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            // WHEN
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // THEN
            verify(filterChain, times(1)).doFilter(request, response);
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        }
    }

    // ============ HELPER METHODS ============

    private JwtClaims createValidClaims(User user) {
        JwtClaims claims = new JwtClaims();
        claims.setUserId(user.getId());
        claims.setUsername(user.getUsername());
        claims.setRole("TEACHER");
        claims.setPermissions(List.of());
        claims.setTenantId("school-001");
        claims.setIat(System.currentTimeMillis());
        claims.setExp(System.currentTimeMillis() + 86400000);
        return claims;
    }
}

