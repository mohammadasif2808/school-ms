package com.school.identity.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Password Reset Token Entity
 *
 * Stores secure, time-bound reset tokens for password recovery
 * Tokens are single-use and expire after a configured duration
 */
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * User associated with this reset token
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Secure reset token (generated cryptographically)
     * Unique and cannot be guessed
     */
    @NotBlank(message = "Reset token is required")
    @Column(unique = true, nullable = false, length = 256)
    private String token;

    /**
     * Token expiration time
     * After this time, token is invalid and cannot be used
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Whether token has been used
     * Once used, token cannot be reused even if not expired
     */
    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

    /**
     * When token was used (if used)
     */
    @Column(name = "used_at")
    private LocalDateTime usedAt;

    /**
     * Created timestamp
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public PasswordResetToken() {
    }

    public PasswordResetToken(User user, String token, LocalDateTime expiresAt) {
        this.user = user;
        this.token = token;
        this.expiresAt = expiresAt;
        this.isUsed = false;
    }

    // Business logic methods

    /**
     * Check if token is still valid (not expired and not used)
     *
     * @return true if token can be used
     */
    public boolean isValid() {
        // Check if already used
        if (Boolean.TRUE.equals(isUsed)) {
            return false;
        }

        // Check if expired
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(expiresAt);
    }

    /**
     * Mark token as used
     */
    public void markAsUsed() {
        this.isUsed = true;
        this.usedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Boolean getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(Boolean isUsed) {
        this.isUsed = isUsed;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

