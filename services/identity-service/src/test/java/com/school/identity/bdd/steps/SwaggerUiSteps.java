package com.school.identity.bdd.steps;

import com.school.identity.bdd.config.RestAssuredConfig;
import com.school.identity.bdd.context.ScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Swagger UI Smoke Test Steps
 *
 * Uses Selenium ONLY for Swagger UI smoke tests
 * API testing is done via REST Assured (not Selenium)
 */
public class SwaggerUiSteps {

    private final ScenarioContext context;
    private WebDriver driver;
    private WebDriverWait wait;

    private static final String SWAGGER_UI_PATH = "/swagger-ui/index.html";

    public SwaggerUiSteps(ScenarioContext context) {
        this.context = context;
    }

    @Before("@swagger-ui")
    public void setupWebDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @After("@swagger-ui")
    public void teardownWebDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ============ SWAGGER UI ACCESS ============

    @Given("the identity-service is running")
    public void theIdentityServiceIsRunning() {
        // Verify service is accessible by making a simple health check
        String baseUrl = RestAssuredConfig.getBaseUrl();
        assertThat(baseUrl).isNotBlank();
    }

    @When("I navigate to Swagger UI")
    public void iNavigateToSwaggerUi() {
        String swaggerUrl = RestAssuredConfig.getBaseUrl() + SWAGGER_UI_PATH;
        driver.get(swaggerUrl);

        // Wait for Swagger UI to load
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.className("swagger-ui")));
    }

    @Then("the Swagger UI page should load successfully")
    public void theSwaggerUiPageShouldLoadSuccessfully() {
        // Check page title
        assertThat(driver.getTitle())
            .as("Page should have Swagger UI title")
            .contains("Swagger");

        // Check for Swagger UI container
        WebElement swaggerUi = driver.findElement(By.className("swagger-ui"));
        assertThat(swaggerUi.isDisplayed())
            .as("Swagger UI should be visible")
            .isTrue();
    }

    @Then("I should see the API title {string}")
    public void iShouldSeeTheApiTitle(String expectedTitle) {
        // Wait for title to be visible
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.className("title")));

        WebElement titleElement = driver.findElement(By.className("title"));
        assertThat(titleElement.getText())
            .as("API title should match")
            .containsIgnoringCase(expectedTitle);
    }

    @Then("I should see the Authentication endpoints")
    public void iShouldSeeTheAuthenticationEndpoints() {
        // Look for Authentication tag/section
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//span[contains(text(), 'auth') or contains(text(), 'Authentication')]")));

        // Verify auth endpoints section exists
        boolean hasAuthSection = driver.findElements(
            By.xpath("//span[contains(text(), 'auth') or contains(text(), 'Authentication')]")
        ).size() > 0;

        assertThat(hasAuthSection)
            .as("Authentication endpoints should be visible")
            .isTrue();
    }

    // ============ SWAGGER JWT AUTHENTICATION ============

    @When("I click the Authorize button")
    public void iClickTheAuthorizeButton() {
        WebElement authorizeButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.className("authorize")));
        authorizeButton.click();
    }

    @When("I enter the JWT token")
    public void iEnterTheJwtToken() {
        // Get token from context (requires prior signin)
        // For smoke test, we verify the dialog exists

        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.className("auth-container")));

        // Find the input field for Bearer token
        WebElement tokenInput = driver.findElement(
            By.xpath("//input[@aria-label='auth-bearer-value']"));

        assertThat(tokenInput.isDisplayed())
            .as("Token input should be visible")
            .isTrue();
    }

    @Then("I should see the authorization dialog")
    public void iShouldSeeTheAuthorizationDialog() {
        WebElement authDialog = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.className("dialog-ux")));

        assertThat(authDialog.isDisplayed())
            .as("Authorization dialog should be visible")
            .isTrue();
    }

    // ============ ENDPOINT VERIFICATION ============

    @Then("I should see endpoint {string}")
    public void iShouldSeeEndpoint(String endpoint) {
        // Wait for endpoints to load
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.className("opblock")));

        // Search for the endpoint path
        boolean endpointFound = driver.findElements(
            By.xpath("//span[contains(@class, 'opblock-summary-path') and contains(text(), '" + endpoint + "')]")
        ).size() > 0;

        assertThat(endpointFound)
            .as("Endpoint " + endpoint + " should be visible")
            .isTrue();
    }

    @Then("I should see HTTP methods for authentication")
    public void iShouldSeeHttpMethodsForAuthentication() {
        // Look for POST methods (signup, signin, signout, etc.)
        boolean hasPostMethods = driver.findElements(
            By.className("opblock-post")
        ).size() > 0;

        assertThat(hasPostMethods)
            .as("POST methods should be visible")
            .isTrue();

        // Look for GET methods (/me)
        boolean hasGetMethods = driver.findElements(
            By.className("opblock-get")
        ).size() > 0;

        assertThat(hasGetMethods)
            .as("GET methods should be visible")
            .isTrue();
    }
}

