package com.school.identity.bdd.config;

import com.school.identity.bdd.client.IdentityApiClient;
import com.school.identity.bdd.context.ScenarioContext;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

/**
 * Cucumber Spring Configuration
 *
 * Provides dependency injection for step definitions
 */
@CucumberContextConfiguration
@TestConfiguration
public class CucumberSpringConfig {

    /**
     * Scenario-scoped context (new instance for each scenario)
     */
    @Bean
    @Scope("cucumber-glue")
    public ScenarioContext scenarioContext() {
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

