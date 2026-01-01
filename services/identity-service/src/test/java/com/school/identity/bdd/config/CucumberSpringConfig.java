package com.school.identity.bdd.config;

import com.school.identity.IdentityServiceApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Cucumber Spring Configuration
 *
 * Configures Spring Boot test context for Cucumber.
 * Explicitly loads BddBeanConfiguration to provide test beans.
 * Uses RANDOM_PORT to avoid port conflicts.
 */
@SpringBootTest(
    classes = {IdentityServiceApplication.class, BddBeanConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@CucumberContextConfiguration
public class CucumberSpringConfig {
    // No bean definitions here - they're in BddBeanConfiguration
    // This class only serves to load the Spring context for Cucumber
}

