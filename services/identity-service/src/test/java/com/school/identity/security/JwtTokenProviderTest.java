package com.school.identity.security;

import com.school.identity.config.JwtProperties;
import com.school.identity.domain.User;
import com.school.identity.dto.JwtClaims;
import com.school.identity.exception.JwtException;
import com.school.identity.testutil.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for JwtTokenProvider
 *
 * Tests JWT token generation, validation, and claim extraction
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JwtTokenProvider Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private JwtProperties jwtProperties;

    // Use a secret key that's at least 32 bytes for HS256
    private static final String TEST_SECRET = "test-secret-key-for-jwt-testing-must-be-long-enough";
    private static final long EXPIRATION_MS = 86400000L; // 24 hours
    private static final String TENANT_ID = "school-001";

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setSecret(TEST_SECRET);
        jwtProperties.setExpiration(EXPIRATION_MS);

        jwtTokenProvider = new JwtTokenProvider(jwtProperties);
    }

    // ============ TOKEN GENERATION TESTS ============

    @Nested
    @DisplayName("Token Generation Tests")
    class TokenGenerationTests {

        @Test
        @DisplayName("GIVEN valid user and permissions WHEN generateToken THEN returns non-empty token")
        void generateToken_givenValidInput_shouldReturnToken() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            List<String> permissions = List.of("STUDENT_VIEW", "ATTENDANCE_MARK");

            // WHEN
            String token = jwtTokenProvider.generateToken(user, permissions, TENANT_ID);

            // THEN
            assertThat(token).isNotNull();
            assertThat(token).isNotBlank();
            assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts
        }

        @Test
        @DisplayName("GIVEN valid user WHEN generateToken THEN token contains correct claims")
        void generateToken_givenValidUser_shouldContainCorrectClaims() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            List<String> permissions = List.of("STUDENT_VIEW", "ATTENDANCE_MARK");

            // WHEN
            String token = jwtTokenProvider.generateToken(user, permissions, TENANT_ID);
            JwtClaims claims = jwtTokenProvider.validateAndExtractClaims(token);

            // THEN
            assertThat(claims.getUserId()).isEqualTo(user.getId());
            assertThat(claims.getUsername()).isEqualTo(user.getUsername());
            assertThat(claims.getPermissions()).containsExactlyInAnyOrderElementsOf(permissions);
            assertThat(claims.getTenantId()).isEqualTo(TENANT_ID);
            assertThat(claims.getIat()).isNotNull();
            assertThat(claims.getExp()).isNotNull();
            assertThat(claims.getExp()).isGreaterThan(claims.getIat());
        }

        @Test
        @DisplayName("GIVEN user with role WHEN generateToken THEN token contains role")
        void generateToken_givenUserWithRole_shouldContainRole() {
            // GIVEN
            User user = TestDataFactory.createUserWithRole(
                TestDataFactory.createRoleWithName("TEACHER"));
            List<String> permissions = List.of("STUDENT_VIEW");

            // WHEN
            String token = jwtTokenProvider.generateToken(user, permissions, TENANT_ID);
            JwtClaims claims = jwtTokenProvider.validateAndExtractClaims(token);

            // THEN
            assertThat(claims.getRole()).isEqualTo("TEACHER");
        }

        @Test
        @DisplayName("GIVEN empty permissions WHEN generateToken THEN token has empty permissions list")
        void generateToken_givenEmptyPermissions_shouldHaveEmptyList() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            List<String> permissions = List.of();

            // WHEN
            String token = jwtTokenProvider.generateToken(user, permissions, TENANT_ID);
            JwtClaims claims = jwtTokenProvider.validateAndExtractClaims(token);

            // THEN
            assertThat(claims.getPermissions()).isEmpty();
        }

        @Test
        @DisplayName("GIVEN token generation WHEN checking expiry THEN expiry is set correctly")
        void generateToken_givenValidInput_shouldSetCorrectExpiry() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            long beforeGeneration = System.currentTimeMillis();

            // WHEN
            String token = jwtTokenProvider.generateToken(user, List.of(), TENANT_ID);
            JwtClaims claims = jwtTokenProvider.validateAndExtractClaims(token);

            // THEN
            long afterGeneration = System.currentTimeMillis();
            assertThat(claims.getIat()).isBetween(beforeGeneration, afterGeneration);
            assertThat(claims.getExp()).isBetween(
                beforeGeneration + EXPIRATION_MS,
                afterGeneration + EXPIRATION_MS
            );
        }
    }

    // ============ TOKEN VALIDATION TESTS ============

    @Nested
    @DisplayName("Token Validation Tests")
    class TokenValidationTests {

        @Test
        @DisplayName("GIVEN valid token WHEN validateAndExtractClaims THEN returns claims")
        void validateToken_givenValidToken_shouldReturnClaims() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            String token = jwtTokenProvider.generateToken(user, List.of("STUDENT_VIEW"), TENANT_ID);

            // WHEN
            JwtClaims claims = jwtTokenProvider.validateAndExtractClaims(token);

            // THEN
            assertThat(claims).isNotNull();
            assertThat(claims.getUserId()).isEqualTo(user.getId());
        }

        @Test
        @DisplayName("GIVEN token with Bearer prefix WHEN validateAndExtractClaims THEN handles correctly")
        void validateToken_givenBearerPrefix_shouldHandleCorrectly() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            String token = jwtTokenProvider.generateToken(user, List.of(), TENANT_ID);
            String tokenWithBearer = "Bearer " + token;

            // WHEN
            JwtClaims claims = jwtTokenProvider.validateAndExtractClaims(tokenWithBearer);

            // THEN
            assertThat(claims).isNotNull();
            assertThat(claims.getUserId()).isEqualTo(user.getId());
        }

        @Test
        @DisplayName("GIVEN malformed token WHEN validateAndExtractClaims THEN throws JwtException")
        void validateToken_givenMalformedToken_shouldThrowException() {
            // GIVEN
            String malformedToken = "not.a.valid.jwt.token";

            // WHEN / THEN
            assertThatThrownBy(() -> jwtTokenProvider.validateAndExtractClaims(malformedToken))
                .isInstanceOf(JwtException.class)
                .hasFieldOrPropertyWithValue("errorCode", "TOKEN_INVALID");
        }

        @Test
        @DisplayName("GIVEN invalid signature WHEN validateAndExtractClaims THEN throws JwtException")
        void validateToken_givenInvalidSignature_shouldThrowException() {
            // GIVEN - Create token with different secret
            JwtProperties otherProperties = new JwtProperties();
            otherProperties.setSecret("different-secret-key-for-testing-invalid-signature");
            otherProperties.setExpiration(EXPIRATION_MS);
            JwtTokenProvider otherProvider = new JwtTokenProvider(otherProperties);

            User user = TestDataFactory.createActiveUser();
            String tokenFromOtherProvider = otherProvider.generateToken(user, List.of(), TENANT_ID);

            // WHEN / THEN - Validate with original provider
            assertThatThrownBy(() -> jwtTokenProvider.validateAndExtractClaims(tokenFromOtherProvider))
                .isInstanceOf(JwtException.class)
                .hasFieldOrPropertyWithValue("errorCode", "TOKEN_INVALID");
        }

        @Test
        @DisplayName("GIVEN empty token WHEN validateAndExtractClaims THEN throws JwtException")
        void validateToken_givenEmptyToken_shouldThrowException() {
            // WHEN / THEN
            assertThatThrownBy(() -> jwtTokenProvider.validateAndExtractClaims(""))
                .isInstanceOf(JwtException.class)
                .hasFieldOrPropertyWithValue("errorCode", "TOKEN_INVALID");
        }

        @Test
        @DisplayName("GIVEN expired token WHEN validateAndExtractClaims THEN throws JwtException")
        void validateToken_givenExpiredToken_shouldThrowException() {
            // GIVEN - Create provider with very short expiration
            JwtProperties shortExpiryProperties = new JwtProperties();
            shortExpiryProperties.setSecret(TEST_SECRET);
            shortExpiryProperties.setExpiration(1L); // 1 millisecond
            JwtTokenProvider shortExpiryProvider = new JwtTokenProvider(shortExpiryProperties);

            User user = TestDataFactory.createActiveUser();
            String token = shortExpiryProvider.generateToken(user, List.of(), TENANT_ID);

            // Wait for token to expire
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // WHEN / THEN
            assertThatThrownBy(() -> shortExpiryProvider.validateAndExtractClaims(token))
                .isInstanceOf(JwtException.class)
                .hasFieldOrPropertyWithValue("errorCode", "TOKEN_EXPIRED");
        }
    }

    // ============ TOKEN VALIDITY CHECK TESTS ============

    @Nested
    @DisplayName("Token Validity Check Tests")
    class TokenValidityCheckTests {

        @Test
        @DisplayName("GIVEN valid token WHEN isTokenValid THEN returns true")
        void isTokenValid_givenValidToken_shouldReturnTrue() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            String token = jwtTokenProvider.generateToken(user, List.of(), TENANT_ID);

            // WHEN
            boolean isValid = jwtTokenProvider.isTokenValid(token);

            // THEN
            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("GIVEN malformed token WHEN isTokenValid THEN returns false")
        void isTokenValid_givenMalformedToken_shouldReturnFalse() {
            // WHEN
            boolean isValid = jwtTokenProvider.isTokenValid("malformed-token");

            // THEN
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("GIVEN empty token WHEN isTokenValid THEN returns false")
        void isTokenValid_givenEmptyToken_shouldReturnFalse() {
            // WHEN
            boolean isValid = jwtTokenProvider.isTokenValid("");

            // THEN
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("GIVEN null token WHEN isTokenValid THEN returns false")
        void isTokenValid_givenNullToken_shouldReturnFalse() {
            // WHEN
            boolean isValid = jwtTokenProvider.isTokenValid(null);

            // THEN
            assertThat(isValid).isFalse();
        }
    }

    // ============ CLAIM EXTRACTION TESTS ============

    @Nested
    @DisplayName("Claim Extraction Tests")
    class ClaimExtractionTests {

        @Test
        @DisplayName("GIVEN valid token WHEN extractUserId THEN returns correct userId")
        void extractUserId_givenValidToken_shouldReturnUserId() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            String token = jwtTokenProvider.generateToken(user, List.of(), TENANT_ID);

            // WHEN
            java.util.UUID userId = jwtTokenProvider.extractUserId(token);

            // THEN
            assertThat(userId).isEqualTo(user.getId());
        }

        @Test
        @DisplayName("GIVEN valid token WHEN extractUsername THEN returns correct username")
        void extractUsername_givenValidToken_shouldReturnUsername() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            String token = jwtTokenProvider.generateToken(user, List.of(), TENANT_ID);

            // WHEN
            String username = jwtTokenProvider.extractUsername(token);

            // THEN
            assertThat(username).isEqualTo(user.getUsername());
        }

        @Test
        @DisplayName("GIVEN valid token WHEN getTimeUntilExpiry THEN returns positive value")
        void getTimeUntilExpiry_givenValidToken_shouldReturnPositiveValue() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            String token = jwtTokenProvider.generateToken(user, List.of(), TENANT_ID);

            // WHEN
            long timeUntilExpiry = jwtTokenProvider.getTimeUntilExpiry(token);

            // THEN
            assertThat(timeUntilExpiry).isPositive();
            assertThat(timeUntilExpiry).isLessThanOrEqualTo(EXPIRATION_MS / 1000);
        }

        @Test
        @DisplayName("GIVEN invalid token WHEN extractUserId THEN throws JwtException")
        void extractUserId_givenInvalidToken_shouldThrowException() {
            // WHEN / THEN
            assertThatThrownBy(() -> jwtTokenProvider.extractUserId("invalid-token"))
                .isInstanceOf(JwtException.class)
                .hasFieldOrPropertyWithValue("errorCode", "TOKEN_INVALID");
        }
    }

    // ============ SECURITY EDGE CASES ============

    @Nested
    @DisplayName("Security Edge Cases")
    class SecurityEdgeCases {

        @Test
        @DisplayName("GIVEN token with modified payload WHEN validate THEN throws exception")
        void validateToken_givenModifiedPayload_shouldThrowException() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();
            String token = jwtTokenProvider.generateToken(user, List.of(), TENANT_ID);

            // Modify the payload part of the token
            String[] parts = token.split("\\.");
            String modifiedToken = parts[0] + ".MODIFIED_PAYLOAD." + parts[2];

            // WHEN / THEN
            assertThatThrownBy(() -> jwtTokenProvider.validateAndExtractClaims(modifiedToken))
                .isInstanceOf(JwtException.class)
                .hasFieldOrPropertyWithValue("errorCode", "TOKEN_INVALID");
        }

        @Test
        @DisplayName("GIVEN token without signature WHEN validate THEN throws exception")
        void validateToken_givenTokenWithoutSignature_shouldThrowException() {
            // GIVEN
            String tokenWithoutSignature = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6InRlc3QifQ.";

            // WHEN / THEN
            assertThatThrownBy(() -> jwtTokenProvider.validateAndExtractClaims(tokenWithoutSignature))
                .isInstanceOf(JwtException.class);
        }

        @Test
        @DisplayName("GIVEN different tokens for same user WHEN generated THEN are different")
        void generateToken_givenSameUserTwice_shouldProduceDifferentTokens() {
            // GIVEN
            User user = TestDataFactory.createActiveUser();

            // WHEN
            String token1 = jwtTokenProvider.generateToken(user, List.of(), TENANT_ID);

            // Small delay to ensure different iat
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            String token2 = jwtTokenProvider.generateToken(user, List.of(), TENANT_ID);

            // THEN - Tokens should be different (different iat/exp)
            assertThat(token1).isNotEqualTo(token2);
        }
    }
}

