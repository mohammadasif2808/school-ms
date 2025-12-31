package com.school.identity.security;

import com.school.identity.config.JwtProperties;
import com.school.identity.domain.User;
import com.school.identity.dto.JwtClaims;
import com.school.identity.exception.JwtException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * JWT Token Provider for generating and validating JWT tokens
 * Handles token creation with required claims and validation
 */
@Component
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        // Initialize the secret key from properties
        this.secretKey = Keys.hmacShaKeyFor(
            jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Generate JWT token from User entity and permissions
     *
     * @param user the authenticated user
     * @param permissions list of permission codes
     * @param tenantId the tenant/school ID
     * @return JWT token string
     * @throws JwtException if token generation fails
     */
    public String generateToken(User user, List<String> permissions, String tenantId) {
        try {
            long now = System.currentTimeMillis();
            long expiryTime = now + jwtProperties.getExpiration();

            String role = extractPrimaryRole(user);

            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId().toString());
            claims.put("username", user.getUsername());
            claims.put("role", role);
            claims.put("permissions", permissions);
            claims.put("tenantId", tenantId);

            return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new java.util.Date(now))
                .setExpiration(new java.util.Date(expiryTime))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
        } catch (JwtException e) {
            throw new JwtException("JWT_GENERATION_ERROR", "Failed to generate JWT token", e);
        }
    }

    /**
     * Validate JWT token and extract claims
     *
     * @param token the JWT token string
     * @return JwtClaims object containing all claims
     * @throws JwtException if token is invalid or expired
     */
    public JwtClaims validateAndExtractClaims(String token) {
        try {
            // Remove "Bearer " prefix if present
            String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;

            Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(cleanToken)
                .getBody();

            // Validate expiration manually for better control
            if (claims.getExpiration().before(new java.util.Date())) {
                throw new JwtException("TOKEN_EXPIRED", "Token has expired");
            }

            JwtClaims jwtClaims = new JwtClaims();
            jwtClaims.setUserId(UUID.fromString((String) claims.get("userId")));
            jwtClaims.setUsername((String) claims.get("username"));
            jwtClaims.setRole((String) claims.get("role"));

            // Handle permissions list
            @SuppressWarnings("unchecked")
            List<String> permissions = (List<String>) claims.get("permissions");
            jwtClaims.setPermissions(permissions);

            jwtClaims.setTenantId((String) claims.get("tenantId"));
            jwtClaims.setIat(claims.getIssuedAt().getTime());
            jwtClaims.setExp(claims.getExpiration().getTime());

            return jwtClaims;
        } catch (ExpiredJwtException e) {
            throw new JwtException("TOKEN_EXPIRED", "Token has expired", e);
        } catch (UnsupportedJwtException e) {
            throw new JwtException("TOKEN_INVALID", "Invalid token format", e);
        } catch (MalformedJwtException e) {
            throw new JwtException("TOKEN_INVALID", "Malformed token", e);
        } catch (SignatureException e) {
            throw new JwtException("TOKEN_INVALID", "Invalid token signature", e);
        } catch (IllegalArgumentException e) {
            throw new JwtException("TOKEN_INVALID", "Token claims are empty", e);
        }
    }

    /**
     * Check if token is still valid (not expired)
     *
     * @param token the JWT token string
     * @return true if token is valid, false otherwise
     */
    public boolean isTokenValid(String token) {
        try {
            String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;

            Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(cleanToken)
                .getBody();

            return !claims.getExpiration().before(new java.util.Date());
        } catch (JwtException | ExpiredJwtException | UnsupportedJwtException |
                 MalformedJwtException | SignatureException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Extract userId from token without full validation
     * Use sparingly, prefer validateAndExtractClaims()
     *
     * @param token the JWT token string
     * @return userId UUID
     * @throws JwtException if extraction fails
     */
    public UUID extractUserId(String token) {
        try {
            String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;

            Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(cleanToken)
                .getBody();

            return UUID.fromString((String) claims.get("userId"));
        } catch (Exception e) {
            throw new JwtException("TOKEN_INVALID", "Failed to extract userId from token", e);
        }
    }

    /**
     * Extract username from token without full validation
     * Use sparingly, prefer validateAndExtractClaims()
     *
     * @param token the JWT token string
     * @return username
     * @throws JwtException if extraction fails
     */
    public String extractUsername(String token) {
        try {
            String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;

            Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(cleanToken)
                .getBody();

            return (String) claims.get("username");
        } catch (Exception e) {
            throw new JwtException("TOKEN_INVALID", "Failed to extract username from token", e);
        }
    }

    /**
     * Calculate token expiry time from now (in seconds)
     *
     * @param token the JWT token string
     * @return remaining seconds until expiry
     * @throws JwtException if extraction fails
     */
    public long getTimeUntilExpiry(String token) {
        try {
            String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;

            Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(cleanToken)
                .getBody();

            long expiryTime = claims.getExpiration().getTime();
            long currentTime = System.currentTimeMillis();
            return Math.max(0, (expiryTime - currentTime) / 1000);
        } catch (Exception e) {
            throw new JwtException("TOKEN_INVALID", "Failed to calculate token expiry", e);
        }
    }

    /**
     * Extract primary role from user entity
     * Currently returns first role name, can be extended for multi-role logic
     *
     * @param user the user entity
     * @return role name or "UNKNOWN" if no roles assigned
     */
    private String extractPrimaryRole(User user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return "UNKNOWN";
        }
        return user.getRoles().stream()
            .findFirst()
            .map(role -> role.getName().toUpperCase())
            .orElse("UNKNOWN");
    }

    /**
     * Get the configured token expiration time in milliseconds
     *
     * @return expiration time in milliseconds
     */
    public Long getTokenExpiration() {
        return jwtProperties.getExpiration();
    }

    /**
     * Get the configured refresh token expiration time in milliseconds
     *
     * @return refresh token expiration time in milliseconds
     */
    public Long getRefreshTokenExpiration() {
        return jwtProperties.getRefreshExpiration();
    }
}

