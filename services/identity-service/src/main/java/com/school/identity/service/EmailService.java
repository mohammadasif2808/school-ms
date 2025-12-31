package com.school.identity.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Email Service (Simulated)
 *
 * In production, this would integrate with an email provider:
 * - AWS SES (Simple Email Service)
 * - SendGrid
 * - Mailgun
 * - Gmail API
 * - Custom SMTP server
 *
 * For now, we simulate by logging to console/file
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    /**
     * Send password reset email (simulated)
     *
     * In production:
     * - Construct HTML email template
     * - Send via email provider (AWS SES, SendGrid, etc)
     * - Track delivery status
     * - Handle bounce backs
     *
     * For now:
     * - Log to console/file
     * - Useful for development/testing
     *
     * @param email user's email address
     * @param firstName user's first name
     * @param resetLink password reset link with token
     */
    public void sendPasswordResetEmail(String email, String firstName, String resetLink) {
        // Simulated email sending (just logging)
        logger.info("=== SIMULATED EMAIL SENT ===");
        logger.info("To: {}", email);
        logger.info("Subject: Password Reset Request");
        logger.info("Body:");
        logger.info("Dear {},", firstName);
        logger.info("You requested to reset your password.");
        logger.info("Click the link below to set a new password:");
        logger.info("Reset Link: {}", resetLink);
        logger.info("This link will expire in 24 hours.");
        logger.info("If you did not request this, ignore this email.");
        logger.info("=============================");
    }

    /**
     * Send password reset confirmation email (simulated)
     *
     * In production:
     * - Send confirmation after password reset
     * - Notify user of account activity
     *
     * For now:
     * - Log to console/file
     *
     * @param email user's email address
     * @param firstName user's first name
     */
    public void sendPasswordResetConfirmation(String email, String firstName) {
        // Simulated email sending (just logging)
        logger.info("=== SIMULATED EMAIL SENT ===");
        logger.info("To: {}", email);
        logger.info("Subject: Password Reset Successful");
        logger.info("Body:");
        logger.info("Dear {},", firstName);
        logger.info("Your password has been successfully reset.");
        logger.info("You can now sign in with your new password.");
        logger.info("If you did not reset your password, contact support immediately.");
        logger.info("=============================");
    }
}

