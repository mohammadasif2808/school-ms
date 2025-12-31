package com.school.identity.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger configuration for identity-service
 *
 * Configures:
 * - API documentation metadata
 * - JWT Bearer token authentication
 * - Security scheme for protected endpoints
 * - Swagger UI with JWT support
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configure OpenAPI documentation with JWT security scheme
     *
     * This allows Swagger UI to accept JWT tokens and automatically
     * include them in requests to protected endpoints
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            // API metadata
            .info(new Info()
                .title("Identity Service API")
                .version("1.0.0")
                .description("Central authentication, authorization, and access-control service for the School Management System")
                .contact(new Contact()
                    .name("School Management System Team")
                    .url("https://school.example.com")
                    .email("support@school.example.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")))

            // Add JWT security scheme
            .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes("BearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT Bearer token authentication. " +
                        "Obtain token from /api/v1/auth/signin endpoint. " +
                        "Enter token without 'Bearer ' prefix in the Swagger UI token field.")));
    }
}

