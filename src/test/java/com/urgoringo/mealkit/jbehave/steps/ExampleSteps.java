package com.urgoringo.mealkit.jbehave.steps;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Example step definitions for JBehave stories.
 * This class demonstrates how to write steps that correspond to story scenarios.
 */
@Component
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
