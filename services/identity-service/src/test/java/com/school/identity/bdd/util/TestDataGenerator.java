package com.school.identity.bdd.util;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test Data Generator
 *
 * Generates unique test data for each scenario to ensure test isolation.
 * Uses timestamp and counter to guarantee uniqueness across parallel runs.
 */
public class TestDataGenerator {

    private static final AtomicInteger counter = new AtomicInteger(0);

    /**
     * Generate unique username
     */
    public static String generateUsername() {
        return "user_" + System.currentTimeMillis() + "_" + counter.incrementAndGet();
    }

    /**
     * Generate unique username with prefix
     */
    public static String generateUsername(String prefix) {
        return prefix + "_" + System.currentTimeMillis() + "_" + counter.incrementAndGet();
    }

    /**
     * Generate unique email
     */
    public static String generateEmail() {
        return "test_" + System.currentTimeMillis() + "_" + counter.incrementAndGet() + "@test.com";
    }

    /**
     * Generate unique email with prefix
     */
    public static String generateEmail(String prefix) {
        return prefix + "_" + System.currentTimeMillis() + "_" + counter.incrementAndGet() + "@test.com";
    }

    /**
     * Generate secure password that meets validation requirements
     * Contains: uppercase, lowercase, digit, special character, 8+ characters
     */
    public static String generateSecurePassword() {
        return "Test@Pass" + counter.incrementAndGet() + "!";
    }

    /**
     * Generate unique phone number
     */
    public static String generatePhone() {
        return "+1" + String.format("%010d", System.currentTimeMillis() % 10000000000L);
    }

    /**
     * Generate unique role name
     */
    public static String generateRoleName() {
        return "ROLE_" + System.currentTimeMillis() + "_" + counter.incrementAndGet();
    }

    /**
     * Generate unique role name with prefix
     */
    public static String generateRoleName(String prefix) {
        return prefix.toUpperCase() + "_" + counter.incrementAndGet();
    }

    /**
     * Generate unique permission code
     */
    public static String generatePermissionCode() {
        return "PERM_" + System.currentTimeMillis() + "_" + counter.incrementAndGet();
    }

    /**
     * Generate unique permission code for module
     */
    public static String generatePermissionCode(String module, String action) {
        return module.toUpperCase() + "_" + action.toUpperCase() + "_" + counter.incrementAndGet();
    }

    /**
     * Generate unique first name
     */
    public static String generateFirstName() {
        return "Test" + counter.incrementAndGet();
    }

    /**
     * Generate unique last name
     */
    public static String generateLastName() {
        return "User" + counter.incrementAndGet();
    }

    /**
     * Generate test data bundle for signup
     */
    public static SignupData generateSignupData() {
        int id = counter.incrementAndGet();
        long timestamp = System.currentTimeMillis();
        return new SignupData(
            "user_" + timestamp + "_" + id,
            "test_" + timestamp + "_" + id + "@test.com",
            "Test@Pass" + id + "!",
            "Test" + id,
            "User" + id,
            "+1" + String.format("%010d", timestamp % 10000000000L)
        );
    }

    /**
     * Generate test data bundle for admin user signup
     */
    public static SignupData generateAdminSignupData() {
        int id = counter.incrementAndGet();
        long timestamp = System.currentTimeMillis();
        return new SignupData(
            "admin_" + timestamp + "_" + id,
            "admin_" + timestamp + "_" + id + "@test.com",
            "Admin@Pass" + id + "!",
            "Admin" + id,
            "User" + id,
            "+1" + String.format("%010d", timestamp % 10000000000L)
        );
    }

    /**
     * Data class for signup information
     */
    public static class SignupData {
        public final String username;
        public final String email;
        public final String password;
        public final String firstName;
        public final String lastName;
        public final String phone;

        public SignupData(String username, String email, String password,
                         String firstName, String lastName, String phone) {
            this.username = username;
            this.email = email;
            this.password = password;
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
        }
    }
}

