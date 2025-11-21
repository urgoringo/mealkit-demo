package com.urgoringo.mealkit.cucumber.steps;

import com.urgoringo.mealkit.cucumber.ApiResponse;
import com.urgoringo.mealkit.cucumber.ApplicationRunner;
import com.urgoringo.mealkit.cucumber.ApplicationRunner.CustomerResponse;
import lombok.RequiredArgsConstructor;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Step definitions for customer signup scenarios.
 */
@RequiredArgsConstructor
public class CustomerSignupSteps {

    private final ApplicationRunner app;
    private String customerEmail;
    private String customerPassword;
    private CustomerResponse customer;
    private ApiResponse<CustomerResponse> response;

    @Before
    public void cleanupDatabase() {
        // Clean up data before each scenario to ensure test isolation
        app.deleteAllSubscriptions();
        app.deleteAllRecipes();
    }

    @When("user signs up using their email and password")
    public void whenUserSignsUpUsingEmailAndPassword() {
        // Generate unique email for this test
        customerEmail = "test-user-" + System.currentTimeMillis() + "@example.com";
        customerPassword = "SecurePassword123!";

        response = app.signupCustomer(customerEmail, customerPassword);
        customer = response.expectSuccess();
    }

    @Then("system creates new customer with used credentials")
    public void thenSystemCreatesNewCustomerWithUsedCredentials() {
        // Verify customer was created
        assertNotNull(customer, "Customer should not be null");
        assertNotNull(customer.id(), "Customer ID should not be null");
        assertEquals(customerEmail, customer.email(), "Customer email should match");

        // Note: We don't return the password in the response for security reasons
        // Password should be hashed and stored securely
    }
}
