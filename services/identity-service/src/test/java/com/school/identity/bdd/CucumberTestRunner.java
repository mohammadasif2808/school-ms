package com.school.identity.bdd;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * Cucumber Test Runner for BDD API Tests
 *
 * Runs all feature files under src/test/resources/features
 *
 * IMPORTANT: These tests require identity-service to be running on localhost:8080
 *
 * Prerequisites:
 * - Start identity-service: mvn spring-boot:run
 * - Database should be EMPTY (tests create their own data)
 *
 * Run tests:
 * - mvn test -Dtest=CucumberTestRunner
 * - Or run from IDE
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports/cucumber.html, json:target/cucumber-reports/cucumber.json")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.school.identity.bdd")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @ignore")
public class CucumberTestRunner {
    // This class serves as the entry point for Cucumber tests
}

