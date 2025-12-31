package com.school.identity.repository;

import com.school.identity.domain.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for PasswordResetToken entity
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    /**
     * Find a password reset token by token string
     *
     * @param token the reset token string
     * @return Optional containing token if found
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Find by token and ensure it's not used
     *
     * @param token the reset token string
     * @return Optional containing token if found and not used
     */
    Optional<PasswordResetToken> findByTokenAndIsUsedFalse(String token);
}

