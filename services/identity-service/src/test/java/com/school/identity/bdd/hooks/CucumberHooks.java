package com.school.identity.bdd.hooks;

import com.school.identity.bdd.client.IdentityApiClient;
import com.school.identity.bdd.config.RestAssuredConfig;
import com.school.identity.bdd.context.ScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

/**
 * Cucumber Hooks
 *
 * Setup and teardown for each scenario.
 * Ensures test isolation by clearing context between scenarios.
 */
public class CucumberHooks {

    private final ScenarioContext scenarioContext;
    private final IdentityApiClient apiClient;

    public CucumberHooks(ScenarioContext scenarioContext, IdentityApiClient apiClient) {
        this.scenarioContext = scenarioContext;
        this.apiClient = apiClient;
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        // Initialize REST Assured configuration
        RestAssuredConfig.configure();

        // Clear any leftover context from previous scenarios
        scenarioContext.clear();

        // Log scenario start (useful for debugging)
        System.out.println("\n========================================");
        System.out.println("STARTING SCENARIO: " + scenario.getName());
        System.out.println("Tags: " + scenario.getSourceTagNames());
        System.out.println("========================================\n");
    }

    @After
    public void afterScenario(Scenario scenario) {
        // Log scenario completion
        System.out.println("\n========================================");
        System.out.println("COMPLETED SCENARIO: " + scenario.getName());
        System.out.println("Status: " + scenario.getStatus());
        System.out.println("========================================\n");

        // Clear context after scenario
        scenarioContext.clear();
    }

    @After("@cleanup")
    public void cleanupTaggedScenarios(Scenario scenario) {
        // Additional cleanup for scenarios tagged with @cleanup
        // This could include deleting test data via API if needed
        System.out.println("Performing cleanup for scenario: " + scenario.getName());
    }
}

