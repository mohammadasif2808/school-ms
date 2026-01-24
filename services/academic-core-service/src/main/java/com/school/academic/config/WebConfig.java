package com.school.academic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Web configuration for academic-core-service
 *
 * Configures:
 * - CORS to allow cross-origin requests from frontend applications
 *
 * Note: This service doesn't use Spring Security, so CORS is configured
 * via CorsFilter bean. The configuration mirrors identity-service's CORS settings.
 */
@Configuration
public class WebConfig {

    /**
     * CORS configuration source bean
     * Matches identity-service's CORS configuration for consistency
     *
     * Allows:
     * - All origins (via patterns) for development
     * - Common HTTP methods (GET, POST, PUT, PATCH, DELETE, OPTIONS)
     * - All headers
     * - Credentials (cookies, authorization headers)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(java.util.Collections.singletonList("*"));
        configuration.setAllowedMethods(java.util.Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * CORS filter bean
     * Since this service doesn't use Spring Security, we need a CorsFilter
     * to apply CORS headers to all requests.
     *
     * @return CorsFilter configured with corsConfigurationSource
     */
    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }
}
