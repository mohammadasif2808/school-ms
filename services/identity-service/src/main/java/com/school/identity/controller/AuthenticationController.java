package com.school.identity.controller;

import com.school.identity.domain.User;
import com.school.identity.dto.SignInRequest;
import com.school.identity.dto.SignInResponse;
import com.school.identity.dto.SignUpRequest;
import com.school.identity.dto.SignUpResponse;
import com.school.identity.exception.AuthenticationException;
import com.school.identity.exception.ValidationException;
import com.school.identity.service.AuthenticationService;
import com.school.identity.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for authentication endpoints
 * Handles sign up, sign in, sign out, and current user retrieval
 *
 * Public API: /api/v1/auth/**
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    public AuthenticationController(AuthenticationService authenticationService, JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    /**
     * Sign Up - User Registration
     *
     * POST /api/v1/auth/signup
     *
     * @param signUpRequest user registration details
     * @return 201 Created with user details
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        try {
            // Delegate to authentication service
            User newUser = authenticationService.signUp(signUpRequest);

            // Map to response DTO
            SignUpResponse response = mapToSignUpResponse(newUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ValidationException e) {
            // 400 Bad Request for validation errors
            return ResponseEntity.badRequest()
                .body(createErrorResponse(e.getErrorCode(), e.getMessage()));
        } catch (AuthenticationException e) {
            // 409 Conflict for duplicate username/email
            if ("USERNAME_EXISTS".equals(e.getErrorCode()) || "EMAIL_EXISTS".equals(e.getErrorCode())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(createErrorResponse(e.getErrorCode(), e.getMessage()));
            }
            // 500 for other auth errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse(e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            // 500 Internal Server Error for unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
        }
    }

    /**
     * Sign In - User Authentication
     *
     * POST /api/v1/auth/signin
     *
     * @param signInRequest username/email and password
     * @return 200 OK with access token and user details
     */
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest signInRequest) {
        try {
            // Delegate to authentication service for credential validation
            User authenticatedUser = authenticationService.signIn(signInRequest);

            // Generate JWT token
            String accessToken = jwtService.generateToken(authenticatedUser);

            // Extract permissions for response
            List<String> permissions = jwtService.extractPermissions(authenticatedUser);

            // Map to response DTO
            SignInResponse response = mapToSignInResponse(authenticatedUser, accessToken, permissions);

            return ResponseEntity.ok(response);
        } catch (ValidationException e) {
            // 400 Bad Request for validation errors
            return ResponseEntity.badRequest()
                .body(createErrorResponse(e.getErrorCode(), e.getMessage()));
        } catch (AuthenticationException e) {
            String errorCode = e.getErrorCode();

            if ("INVALID_CREDENTIALS".equals(errorCode)) {
                // 401 Unauthorized for invalid credentials
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse(errorCode, e.getMessage()));
            } else if ("ACCOUNT_INACTIVE".equals(errorCode)) {
                // 403 Forbidden for inactive account
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(errorCode, e.getMessage()));
            } else if ("ACCOUNT_BLOCKED".equals(errorCode)) {
                // 403 Forbidden for blocked account
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(errorCode, e.getMessage()));
            }

            // 500 for other auth errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse(errorCode, e.getMessage()));
        } catch (Exception e) {
            // 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
        }
    }

    /**
     * Sign Out - User Logout
     *
     * POST /api/v1/auth/signout
     *
     * @param authHeader Authorization header with Bearer token
     * @return 200 OK with success message
     */
    @PostMapping("/signout")
    public ResponseEntity<?> signOut(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Validate token if provided
            if (authHeader != null && !authHeader.isEmpty()) {
                jwtService.isTokenValid(authHeader);
            }

            // Return success response
            return ResponseEntity.ok(createMessageResponse("Successfully signed out"));
        } catch (Exception e) {
            // 401 Unauthorized for invalid token
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(createErrorResponse("UNAUTHORIZED", "Invalid or expired token"));
        }
    }

    /**
     * Get Current Authenticated User
     *
     * GET /api/v1/auth/me
     *
     * Requires: USER_VIEW permission
     *
     * @param authHeader Authorization header with Bearer token
     * @return 200 OK with current user profile and permissions
     */
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'USER_VIEW')")
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Validate token is present
            if (authHeader == null || authHeader.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("UNAUTHORIZED", "Token required"));
            }

            // Validate token and fetch user
            User currentUser = jwtService.validateTokenAndGetUser(authHeader);

            // Extract permissions
            List<String> permissions = jwtService.extractPermissions(currentUser);
            String primaryRole = jwtService.extractPrimaryRole(currentUser);

            // Map to response DTO
            var response = mapToCurrentUserResponse(currentUser, permissions, primaryRole);

            return ResponseEntity.ok(response);
        } catch (com.school.identity.exception.JwtException e) {
            String errorCode = e.getErrorCode();

            if ("TOKEN_EXPIRED".equals(errorCode)) {
                // 401 Unauthorized for expired token
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse(errorCode, e.getMessage()));
            } else if ("TOKEN_INVALID".equals(errorCode)) {
                // 401 Unauthorized for invalid token
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse(errorCode, e.getMessage()));
            } else if ("USER_NOT_FOUND".equals(errorCode)) {
                // 404 Not Found if user deleted
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(errorCode, e.getMessage()));
            } else if ("USER_DELETED".equals(errorCode)) {
                // 403 Forbidden if user deleted
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(errorCode, e.getMessage()));
            }

            // 500 for other JWT errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse(errorCode, e.getMessage()));
        } catch (Exception e) {
            // 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
        }
    }

    // ============ Helper Methods ============

    /**
     * Map User entity to SignUpResponse DTO
     */
    private SignUpResponse mapToSignUpResponse(User user) {
        SignUpResponse response = new SignUpResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirst_name(user.getFirstName());
        response.setLast_name(user.getLastName());
        response.setPhone(user.getPhone());
        response.setStatus(user.getStatus());
        response.setCreated_at(user.getCreatedAt());
        return response;
    }

    /**
     * Map User entity to SignInResponse DTO with token
     */
    private SignInResponse mapToSignInResponse(User user, String accessToken, List<String> permissions) {
        SignInResponse response = new SignInResponse();
        response.setAccessToken(accessToken);

        // Create user info object
        SignInResponse.UserInfo userInfo = new SignInResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setFirst_name(user.getFirstName());
        userInfo.setLast_name(user.getLastName());
        userInfo.setAvatar_url(user.getAvatarUrl());
        userInfo.setStatus(user.getStatus());

        response.setUser(userInfo);
        return response;
    }

    /**
     * Map User entity to CurrentUserResponse DTO
     */
    private Object mapToCurrentUserResponse(User user, List<String> permissions, String primaryRole) {
        var response = new java.util.LinkedHashMap<String, Object>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("first_name", user.getFirstName());
        response.put("last_name", user.getLastName());
        response.put("phone", user.getPhone());
        response.put("avatar_url", user.getAvatarUrl());
        response.put("is_super_admin", user.getIsSuperAdmin());
        response.put("status", user.getStatus());
        response.put("role", primaryRole);
        response.put("permissions", permissions);
        response.put("created_at", user.getCreatedAt());
        return response;
    }

    /**
     * Create error response DTO
     */
    private Object createErrorResponse(String error, String message) {
        var errorResponse = new java.util.LinkedHashMap<String, Object>();
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        return errorResponse;
    }

    /**
     * Create message response DTO
     */
    private Object createMessageResponse(String message) {
        var response = new java.util.LinkedHashMap<String, String>();
        response.put("message", message);
        return response;
    }
}

