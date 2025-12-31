package com.school.identity.service;

import com.school.identity.domain.PasswordResetToken;
import com.school.identity.domain.User;
import com.school.identity.dto.ForgotPasswordRequest;
import com.school.identity.dto.ResetPasswordRequest;
import com.school.identity.exception.ValidationException;
import com.school.identity.repository.PasswordResetTokenRepository;
import com.school.identity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

/**
 * Service for password reset functionality
 *
 * Handles:
 * - Generating secure reset tokens
 * - Storing reset tokens with expiration
 * - Validating reset tokens (not expired, not used)
 * - Updating user password
 * - Single-use token enforcement
 */
@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    /**
     * Reset token validity duration in hours
     * Default: 24 hours (configurable via environment)
     */
    @Value("${password-reset.token-expiry-hours:24}")
    private int tokenExpiryHours;

    /**
     * Reset token length in bytes
     * 32 bytes = 256 bits of entropy (very secure)
     */
    private static final int TOKEN_LENGTH_BYTES = 32;

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    /**
     * Initiate password reset process
     *
     * 1. Find user by email
     * 2. Generate secure reset token
     * 3. Save token to database with expiration
     * 4. Send reset link via email (simulated)
     *
     * @param request ForgotPasswordRequest with user email
     * @throws ValidationException if email not found
     */
    @Transactional
    public void requestPasswordReset(ForgotPasswordRequest request) {
        String email = request.getEmail();

        // Find user by email
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            // Don't reveal if email exists or not (security best practice)
            // Just return success message
            return;
        }

        User user = userOpt.get();

        // Check if user is deleted
        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            return;  // Silently fail
        }

        // Generate secure reset token
        String resetToken = generateSecureToken();

        // Calculate expiration time
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(tokenExpiryHours);

        // Create and save reset token
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, resetToken, expiresAt);
        tokenRepository.save(passwordResetToken);

        // Send reset link via email (simulated)
        String resetLink = buildResetLink(resetToken);
        emailService.sendPasswordResetEmail(user.getEmail(), user.getFirstName(), resetLink);
    }

    /**
     * Complete password reset process
     *
     * 1. Find and validate reset token
     * 2. Check token is not expired
     * 3. Check token not already used
     * 4. Hash new password
     * 5. Update user's password
     * 6. Mark token as used
     *
     * @param request ResetPasswordRequest with token and new password
     * @throws ValidationException if token invalid/expired/used
     * @throws ValidationException if password too weak
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String token = request.getToken();
        String newPassword = request.getNewPassword();

        // Validate new password strength
        validatePasswordStrength(newPassword);

        // Find reset token (unused only)
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByTokenAndIsUsedFalse(token);

        if (tokenOpt.isEmpty()) {
            // Token doesn't exist or already used
            throw new ValidationException(
                "INVALID_RESET_TOKEN",
                "Invalid or expired reset token"
            );
        }

        PasswordResetToken resetToken = tokenOpt.get();

        // Validate token is not expired
        if (!resetToken.isValid()) {
            throw new ValidationException(
                "INVALID_RESET_TOKEN",
                "Reset token has expired"
            );
        }

        // Get user
        User user = resetToken.getUser();

        // Check user still exists and not deleted
        if (user == null || Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new ValidationException(
                "USER_NOT_FOUND",
                "User not found"
            );
        }

        // Hash new password
        String hashedPassword = passwordEncoder.encode(newPassword);

        // Update user password
        user.setPasswordHash(hashedPassword);
        user.setLastModifiedAt(LocalDateTime.now());
        userRepository.save(user);

        // Mark token as used (single-use enforcement)
        resetToken.markAsUsed();
        tokenRepository.save(resetToken);
    }

    /**
     * Generate a secure random reset token
     *
     * Uses SecureRandom with Base64 encoding
     * Produces URL-safe token that cannot be guessed
     *
     * @return secure reset token string
     */
    private String generateSecureToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[TOKEN_LENGTH_BYTES];
        secureRandom.nextBytes(tokenBytes);

        // Use URL-safe Base64 encoding
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(tokenBytes);
    }

    /**
     * Build password reset link for email
     * In production, this would be a frontend URL with token
     *
     * @param token reset token
     * @return reset link
     */
    private String buildResetLink(String token) {
        // Frontend URL would be from configuration
        // Example: https://school.example.com/reset-password?token={token}
        return "https://app.example.com/reset-password?token=" + token;
    }

    /**
     * Validate password strength
     *
     * Requirements:
     * - Minimum 8 characters
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one digit
     * - At least one special character
     *
     * @param password password to validate
     * @throws ValidationException if password doesn't meet requirements
     */
    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new ValidationException(
                "PASSWORD_WEAK",
                "Password must be at least 8 characters"
            );
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new ValidationException(
                "PASSWORD_WEAK",
                "Password must contain at least one uppercase letter"
            );
        }

        if (!password.matches(".*[a-z].*")) {
            throw new ValidationException(
                "PASSWORD_WEAK",
                "Password must contain at least one lowercase letter"
            );
        }

        if (!password.matches(".*\\d.*")) {
            throw new ValidationException(
                "PASSWORD_WEAK",
                "Password must contain at least one digit"
            );
        }

        if (!password.matches(".*[@$!%*?&].*")) {
            throw new ValidationException(
                "PASSWORD_WEAK",
                "Password must contain at least one special character (@$!%*?&)"
            );
        }
    }

    /**
     * Get token expiry duration in hours
     *
     * @return hours until token expiration
     */
    public int getTokenExpiryHours() {
        return tokenExpiryHours;
    }
}

