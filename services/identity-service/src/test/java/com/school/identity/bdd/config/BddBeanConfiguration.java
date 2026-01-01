package com.school.identity.bdd.config;

import com.school.identity.bdd.client.IdentityApiClient;
import com.school.identity.bdd.context.ScenarioContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * BDD Test Configuration for Spring Beans
 *
 * This is separate from CucumberSpringConfig to avoid Cucumber trying to use it as a glue class.
 * It provides the beans needed by step definitions without conflicting with Cucumber's Spring integration.
 */
@Configuration
public class BddBeanConfiguration {

    @Value("${local.server.port:8080}")
    private int serverPort;

    /**
     * Configure REST Assured with the actual server port
     * Called after Spring injects the serverPort
     */
    public void configureRestAssured() {
        String baseUrl = "http://localhost:" + serverPort;
        RestAssuredConfig.setBaseUrl(baseUrl);
    }

    /**
     * Scenario-scoped context (one instance per scenario)
     * Using "cucumber-glue" scope which persists across all steps in a scenario
     */
    @Bean
    @Scope("cucumber-glue")
    public ScenarioContext scenarioContext() {
        configureRestAssured();  // Configure REST Assured on first bean creation
        return new ScenarioContext();
    }

    /**
     * API Client (singleton, stateless)
     */
    @Bean
    public IdentityApiClient identityApiClient() {
        return new IdentityApiClient();
    }
}

