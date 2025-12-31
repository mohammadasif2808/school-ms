package com.school.identity.service;

import com.school.identity.domain.User;
import com.school.identity.domain.UserStatus;
import com.school.identity.dto.SignInRequest;
import com.school.identity.dto.SignUpRequest;
import com.school.identity.exception.AuthenticationException;
import com.school.identity.exception.ValidationException;
import com.school.identity.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Password complexity pattern: at least 1 uppercase, 1 lowercase, 1 digit, 1 special char
    private static final Pattern PASSWORD_PATTERN =
        Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user (Sign Up)
     *
     * @param signUpRequest the signup request containing user details and password
     * @return the created User entity
     * @throws IllegalArgumentException if validation fails
     */
    public User signUp(SignUpRequest signUpRequest) {
        // Validate request fields
        validateSignUpRequest(signUpRequest);

        // Check if username already exists
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new AuthenticationException("USERNAME_EXISTS", "Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new AuthenticationException("EMAIL_EXISTS", "Email already exists");
        }

        // Validate password complexity
        validatePasswordStrength(signUpRequest.getPassword());

        // Create new user
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setFirstName(signUpRequest.getFirst_name());
        user.setLastName(signUpRequest.getLast_name());
        user.setPhone(signUpRequest.getPhone());

        // Hash password using Spring Security's PasswordEncoder
        user.setPasswordHash(passwordEncoder.encode(signUpRequest.getPassword()));

        // New users are ACTIVE by default
        user.setStatus(UserStatus.ACTIVE);
        user.setIsSuperAdmin(false);
        user.setIsDeleted(false);

        return userRepository.save(user);
    }

    /**
     * Authenticate user (Sign In)
     *
     * @param signInRequest the signin request containing username/email and password
     * @return the authenticated User entity if credentials are valid
     * @throws IllegalArgumentException if validation or authentication fails
     */
    public User signIn(SignInRequest signInRequest) {
        // Validate request fields
        validateSignInRequest(signInRequest);

        // Find user by username or email
        Optional<User> userOptional = userRepository.findByUsername(signInRequest.getUsername())
            .or(() -> userRepository.findByEmail(signInRequest.getUsername()));

        if (userOptional.isEmpty()) {
            throw new AuthenticationException("INVALID_CREDENTIALS", "Invalid username or password");
        }

        User user = userOptional.get();

        // Check if user is soft-deleted
        if (user.getIsDeleted()) {
            throw new AuthenticationException("INVALID_CREDENTIALS", "Invalid username or password");
        }

        // Check user status
        validateUserStatus(user);

        // Verify password
        if (!passwordEncoder.matches(signInRequest.getPassword(), user.getPasswordHash())) {
            throw new AuthenticationException("INVALID_CREDENTIALS", "Invalid username or password");
        }

        return user;
    }

    /**
     * Validate sign up request fields
     *
     * @param request the signup request
     * @throws ValidationException if any field is invalid
     */
    private void validateSignUpRequest(SignUpRequest request) {
        if (request == null) {
            throw new ValidationException("VALIDATION_ERROR", "Request body is required");
        }

        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new ValidationException("VALIDATION_ERROR", "Username is required");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ValidationException("VALIDATION_ERROR", "Email is required");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ValidationException("VALIDATION_ERROR", "Password is required");
        }

        if (request.getFirst_name() == null || request.getFirst_name().isBlank()) {
            throw new ValidationException("VALIDATION_ERROR", "First name is required");
        }

        if (request.getLast_name() == null || request.getLast_name().isBlank()) {
            throw new ValidationException("VALIDATION_ERROR", "Last name is required");
        }

        if (request.getPhone() == null || request.getPhone().isBlank()) {
            throw new ValidationException("VALIDATION_ERROR", "Phone is required");
        }
    }

    /**
     * Validate sign in request fields
     *
     * @param request the signin request
     * @throws ValidationException if any field is invalid
     */
    private void validateSignInRequest(SignInRequest request) {
        if (request == null) {
            throw new ValidationException("VALIDATION_ERROR", "Request body is required");
        }

        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new ValidationException("VALIDATION_ERROR", "Username or email is required");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ValidationException("VALIDATION_ERROR", "Password is required");
        }
    }

    /**
     * Validate password strength
     * Password must contain: uppercase, lowercase, digit, special character
     * Minimum 8 characters
     *
     * @param password the password to validate
     * @throws ValidationException if password is weak
     */
    private void validatePasswordStrength(String password) {
        if (password == null || password.isBlank()) {
            throw new ValidationException("VALIDATION_ERROR", "Password is required");
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new ValidationException("PASSWORD_WEAK",
                "Password must contain uppercase, lowercase, digit, and special character");
        }
    }

    /**
     * Validate user account status
     * User must be ACTIVE to sign in
     *
     * @param user the user to check
     * @throws AuthenticationException if user is inactive or blocked
     */
    private void validateUserStatus(User user) {
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new AuthenticationException("ACCOUNT_INACTIVE", "User account is not active");
        }

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new AuthenticationException("ACCOUNT_BLOCKED", "User account is blocked");
        }
    }

    /**
     * Check if a user exists by username (not soft-deleted)
     *
     * @param username the username to check
     * @return true if user exists and is not deleted
     */
    public boolean userExists(String username) {
        return userRepository.findByUsernameAndIsDeletedFalse(username).isPresent();
    }

    /**
     * Check if a user exists by email (not soft-deleted)
     *
     * @param email the email to check
     * @return true if user exists and is not deleted
     */
    public boolean emailExists(String email) {
        return userRepository.findByEmailAndIsDeletedFalse(email).isPresent();
    }

    /**
     * Retrieve user by username (not soft-deleted)
     *
     * @param username the username
     * @return Optional containing the user if found
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsernameAndIsDeletedFalse(username);
    }

    /**
     * Retrieve user by email (not soft-deleted)
     *
     * @param email the email
     * @return Optional containing the user if found
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmailAndIsDeletedFalse(email);
    }

    /**
     * Retrieve user by ID (not soft-deleted)
     *
     * @param userId the user ID
     * @return Optional containing the user if found
     */
    public Optional<User> getUserById(UUID userId) {
        return userRepository.findById(userId)
            .filter(user -> !user.getIsDeleted());
    }
}

