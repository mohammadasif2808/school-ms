package com.school.identity.service;

import com.school.identity.domain.User;
import com.school.identity.dto.JwtClaims;
import com.school.identity.exception.JwtException;
import com.school.identity.repository.UserRepository;
import com.school.identity.security.JwtClaimsBuilder;
import com.school.identity.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JWT Service for high-level token operations
 * Integrates JwtTokenProvider and JwtClaimsBuilder for complete JWT workflow
 */
@Service
public class JwtService {

    private final JwtTokenProvider tokenProvider;
    private final JwtClaimsBuilder claimsBuilder;
    private final UserRepository userRepository;

    @Value("${service.tenant-id:default}")
    private String tenantId;

    public JwtService(JwtTokenProvider tokenProvider, JwtClaimsBuilder claimsBuilder,
                      UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.claimsBuilder = claimsBuilder;
        this.userRepository = userRepository;
    }

    /**
     * Generate JWT token for authenticated user
     * Extracts roles and permissions from user entity
     *
     * @param user the authenticated user entity
     * @return JWT token string
     * @throws JwtException if token generation fails
     */
    public String generateToken(User user) {
        try {
            // Extract permissions from user's roles
            List<String> permissions = claimsBuilder.extractPermissionsFromRoles(user);

            // Generate token using token provider
            return tokenProvider.generateToken(user, permissions, tenantId);
        } catch (Exception e) {
            throw new JwtException("JWT_GENERATION_ERROR",
                "Failed to generate JWT token for user: " + user.getUsername(), e);
        }
    }

    /**
     * Validate JWT token and extract claims
     *
     * @param token the JWT token string (with or without Bearer prefix)
     * @return JwtClaims object with all claims
     * @throws JwtException if token is invalid or expired
     */
    public JwtClaims validateToken(String token) {
        try {
            return tokenProvider.validateAndExtractClaims(token);
        } catch (JwtException e) {
            throw e;
        } catch (Exception e) {
            throw new JwtException("TOKEN_INVALID", "Failed to validate token", e);
        }
    }

    /**
     * Check if token is valid without throwing exception
     *
     * @param token the JWT token string
     * @return true if token is valid and not expired
     */
    public boolean isTokenValid(String token) {
        return tokenProvider.isTokenValid(token);
    }

    /**
     * Validate token and fetch user from database
     * Ensures user still exists and has valid status
     *
     * @param token the JWT token string
     * @return User entity if valid and found
     * @throws JwtException if token invalid or user not found
     */
    public User validateTokenAndGetUser(String token) {
        try {
            JwtClaims claims = validateToken(token);

            Optional<User> user = userRepository.findById(claims.getUserId());

            if (user.isEmpty()) {
                throw new JwtException("USER_NOT_FOUND",
                    "User not found for userId: " + claims.getUserId());
            }

            User foundUser = user.get();

            // Check if user is deleted
            if (foundUser.getIsDeleted()) {
                throw new JwtException("USER_DELETED", "User has been deleted");
            }

            return foundUser;
        } catch (JwtException e) {
            throw e;
        } catch (Exception e) {
            throw new JwtException("TOKEN_VALIDATION_ERROR", "Failed to validate token and fetch user", e);
        }
    }

    /**
     * Extract userId from token
     *
     * @param token the JWT token string
     * @return userId UUID
     * @throws JwtException if extraction fails
     */
    public UUID extractUserId(String token) {
        return tokenProvider.extractUserId(token);
    }

    /**
     * Extract username from token
     *
     * @param token the JWT token string
     * @return username
     * @throws JwtException if extraction fails
     */
    public String extractUsername(String token) {
        return tokenProvider.extractUsername(token);
    }

    /**
     * Get remaining time until token expiry (in seconds)
     *
     * @param token the JWT token string
     * @return remaining seconds until expiry
     * @throws JwtException if extraction fails
     */
    public long getTimeUntilExpiry(String token) {
        return tokenProvider.getTimeUntilExpiry(token);
    }

    /**
     * Check if token is expiring soon (within threshold)
     * Useful for token refresh logic
     *
     * @param token the JWT token string
     * @param thresholdSeconds threshold in seconds
     * @return true if token will expire within threshold
     */
    public boolean isTokenExpiringSoon(String token, long thresholdSeconds) {
        try {
            long remainingTime = getTimeUntilExpiry(token);
            return remainingTime < thresholdSeconds;
        } catch (JwtException e) {
            return true; // Treat as expiring if we can't determine
        }
    }

    /**
     * Get token expiration time in milliseconds
     *
     * @return expiration time in milliseconds
     */
    public Long getTokenExpirationMs() {
        return tokenProvider.getTokenExpiration();
    }

    /**
     * Check if user has specific permission
     *
     * @param user the user entity
     * @param permissionCode the permission code to check
     * @return true if user has this permission
     */
    public boolean hasPermission(User user, String permissionCode) {
        return claimsBuilder.hasPermission(user, permissionCode);
    }

    /**
     * Check if user is super admin
     *
     * @param user the user entity
     * @return true if user is super admin
     */
    public boolean isSuperAdmin(User user) {
        return claimsBuilder.isSuperAdmin(user);
    }

    /**
     * Extract primary role name from user
     *
     * @param user the user entity
     * @return role name
     */
    public String extractPrimaryRole(User user) {
        return claimsBuilder.extractPrimaryRoleName(user);
    }

    /**
     * Extract all permissions from user
     *
     * @param user the user entity
     * @return list of permission codes
     */
    public List<String> extractPermissions(User user) {
        return claimsBuilder.extractPermissionsFromRoles(user);
    }

    /**
     * Get all role names for user (comma-separated)
     *
     * @param user the user entity
     * @return comma-separated role names
     */
    public String getAllRoleNames(User user) {
        return claimsBuilder.getAllRoleNames(user);
    }
}

