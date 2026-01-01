package com.school.identity.security;

import com.school.identity.domain.User;
import com.school.identity.dto.JwtClaims;
import com.school.identity.exception.JwtException;
import com.school.identity.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT Authentication Filter
 *
 * Responsible for:
 * - Extracting JWT token from Authorization header
 * - Validating JWT token signature and expiration
 * - Fetching user from database to verify not deleted
 * - Populating SecurityContext with authenticated user
 * - Passing request to next filter if valid token
 * - Allowing request to continue if no token (public endpoint decision made by Spring Security)
 *
 * This filter runs BEFORE Spring Security's authentication filter,
 * so it prepares authentication info but doesn't reject requests.
 * Rejection happens based on @authorizeHttpRequests rules.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Filter logic:
     * 1. Extract token from Authorization header
     * 2. If no token, allow request to continue (public endpoint or Spring Security will reject)
     * 3. Validate token and get user
     * 4. Create Authentication token
     * 5. Populate SecurityContext
     * 6. Continue filter chain
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param filterChain filter chain
     * @throws ServletException if filter error
     * @throws IOException if IO error
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Extract token from Authorization header
            String token = extractTokenFromRequest(request);

            // If no token, allow request to continue
            // Spring Security will decide if authentication is required based on @authorizeHttpRequests
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // Validate token and fetch user
            User user = jwtService.validateTokenAndGetUser(token);

            // Create authentication token
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    new ArrayList<>() // Authorities are empty (no role-based auth yet)
                );

            // Set authentication in SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Continue filter chain
            filterChain.doFilter(request, response);

        } catch (JwtException e) {
            // JWT validation failed
            // Clear any existing authentication
            SecurityContextHolder.clearContext();

            // Continue filter chain
            // Spring Security will reject request if endpoint requires authentication
            // Our JwtAuthenticationEntryPoint will return 401 Unauthorized
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // Unexpected error
            // Clear any existing authentication
            SecurityContextHolder.clearContext();

            // Continue filter chain
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Extract JWT token from Authorization header
     *
     * Expected format: "Bearer <token>"
     *
     * @param request HTTP request
     * @return JWT token string, or null if not present
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || authHeader.isEmpty()) {
            return null;
        }

        // Check for "Bearer " prefix
        if (!authHeader.startsWith("Bearer ")) {
            return null;
        }

        // Extract token (skip "Bearer " prefix)
        return authHeader.substring(7);
    }
}

