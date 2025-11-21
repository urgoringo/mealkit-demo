package com.urgoringo.mealkit.cucumber.steps;

import com.urgoringo.mealkit.cucumber.ApiResponse;
import com.urgoringo.mealkit.cucumber.ApplicationRunner;
import com.urgoringo.mealkit.cucumber.ApplicationRunner.CustomerResponse;
import com.urgoringo.mealkit.cucumber.ApplicationRunner.LoginResponse;
import com.urgoringo.mealkit.cucumber.ApplicationRunner.SubscriptionResponse;
import lombok.RequiredArgsConstructor;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Step definitions for customer authentication scenarios.
 */
@RequiredArgsConstructor
public class CustomerSignupSteps {

    private final ApplicationRunner app;
    private String customerEmail;
    private String customerPassword;
    private CustomerResponse customer;
    private ApiResponse<CustomerResponse> signupResponse;
    private ApiResponse<LoginResponse> loginResponse;
    private LoginResponse loginResult;
    private Long customerId;
    private SubscriptionResponse subscription;

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

        signupResponse = app.signupCustomer(customerEmail, customerPassword);
        customer = signupResponse.expectSuccess();
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

    @Given("customer with email {word} exists and has a subscripion")
    public void givenCustomerWithEmailExistsAndHasASubscription(String email) {
        customerEmail = email;
        customerPassword = "TestPassword123!";

        // Sign up the customer
        customer = app.signupCustomer(customerEmail, customerPassword).expectSuccess();
        customerId = customer.id();

        // Create 3 recipes for the subscription
        List<Long> recipeIds = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            var recipe = app.createRecipe("Recipe " + i).expectSuccess();
            recipeIds.add(recipe.id());
        }

        // Create a subscription for the customer
        String address = "123 Main St\n12345 New York\nUSA";
        app.createSubscription(customerEmail, recipeIds, address).expectSuccess();
    }

    @When("they log in using their email and password")
    public void whenTheyLogInUsingEmailAndPassword() {
        loginResponse = app.loginCustomer(customerEmail, customerPassword);
        loginResult = loginResponse.expectSuccess();
    }

    @Then("they can access their subscription")
    public void thenTheyCanAccessTheirSubscription() {
        // Verify login was successful and returns authentication token
        assertNotNull(loginResult, "Login result should not be null");
        assertNotNull(loginResult.token(), "Authentication token should not be null");

        // Use the authentication token to get subscription
        // Token contains customer identity, server validates and returns the customer's subscription
        subscription = app.getMySubscription(loginResult.token()).expectSuccess();

        // Verify customer can access their subscription
        assertNotNull(subscription, "Subscription should not be null");
        assertNotNull(subscription.id(), "Subscription ID should not be null");
        assertNotNull(subscription.customerId(), "Customer ID should not be null");
        assertEquals(customerId, subscription.customerId(),
                "Subscription should belong to the logged-in customer");
    }
}
