package com.school.identity.bdd.config;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * REST Assured Configuration for API Tests
 *
 * Configures base URL, content types, and logging
 */
public class RestAssuredConfig {

    // Service configuration (can be overridden via environment variables)
    private static String BASE_URL = System.getenv().getOrDefault(
        "IDENTITY_SERVICE_URL", "http://localhost:8080");

    private static final String API_BASE_PATH = "/api/v1";

    private static boolean configured = false;

    /**
     * Set the base URL dynamically (used for RANDOM_PORT in tests)
     */
    public static void setBaseUrl(String url) {
        BASE_URL = url;
        configured = false; // Reset configuration flag so it reconfigures with new URL
    }

    /**
     * Initialize REST Assured configuration (call once before tests)
     */
    public static void configure() {
        if (!configured) {
            RestAssured.baseURI = BASE_URL;
            RestAssured.basePath = API_BASE_PATH;
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
            configured = true;
        }
    }

    /**
     * Get base request specification with JSON content type
     */
    public static RequestSpecification getBaseSpec() {
        configure();
        return new RequestSpecBuilder()
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .build();
    }

    /**
     * Get authenticated request specification with JWT token
     */
    public static RequestSpecification getAuthenticatedSpec(String jwtToken) {
        configure();
        return new RequestSpecBuilder()
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .addHeader("Authorization", "Bearer " + jwtToken)
            .build();
    }

    /**
     * Get base URL for service
     */
    public static String getBaseUrl() {
        return BASE_URL;
    }

    /**
     * Get full API path
     */
    public static String getFullApiPath(String endpoint) {
        return BASE_URL + API_BASE_PATH + endpoint;
    }
}

