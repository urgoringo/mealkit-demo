package com.urgoringo.mealkit.cucumber.steps;

import lombok.RequiredArgsConstructor;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Example step definitions for Cucumber scenarios.
 * This class demonstrates how to write steps that correspond to feature scenarios.
 */
@RequiredArgsConstructor
public class ExampleSteps {

    private final ApplicationContext context;
    private final TestRestTemplate restTemplate;
    private boolean applicationRunning = false;
    private boolean statusChecked = false;

    @Given("the Mealkit application is running")
    public void givenApplicationIsRunning() {
        applicationRunning = context != null;
    }

    @When("I check the application status")
    public void whenICheckStatus() {
        if (applicationRunning) {
            try {
                ResponseEntity<String> response = restTemplate.getForEntity("/health", String.class);
                statusChecked = response.getStatusCode() == HttpStatus.OK;
            } catch (Exception e) {
                statusChecked = false;
            }
        }
    }

    @Then("the application should be healthy")
    public void thenApplicationShouldBeHealthy() {
        assertTrue(applicationRunning && statusChecked, "Application should be running and status should be checked");
    }
}
