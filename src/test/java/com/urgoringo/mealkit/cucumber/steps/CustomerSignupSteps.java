package com.urgoringo.mealkit.cucumber.steps;

import com.urgoringo.mealkit.cucumber.ApplicationRunner;
import com.urgoringo.mealkit.cucumber.ApplicationRunner.CustomerResponse;
import com.urgoringo.mealkit.cucumber.ApplicationRunner.LoginResponse;
import com.urgoringo.mealkit.cucumber.ApplicationRunner.SubscriptionResponse;
import lombok.RequiredArgsConstructor;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.stream.IntStream;

import static com.urgoringo.mealkit.cucumber.scaffolding.TestFactory.aPassword;
import static com.urgoringo.mealkit.cucumber.scaffolding.TestFactory.anEmail;
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
    private LoginResponse loginResult;
    private Long customerId;

    @Before
    public void cleanupDatabase() {
        app.deleteAllSubscriptions();
        app.deleteAllRecipes();
    }

    @When("user signs up using their email and password")
    public void whenUserSignsUpUsingEmailAndPassword() {
        customerEmail = anEmail();
        customerPassword = aPassword();

        var signupResponse = app.signupCustomer(customerEmail, customerPassword);
        customer = signupResponse.expectSuccess();
    }

    @Then("system creates new customer with used credentials")
    public void thenSystemCreatesNewCustomerWithUsedCredentials() {
        assertNotNull(customer, "Customer should not be null");
        assertNotNull(customer.id(), "Customer ID should not be null");
        assertEquals(customerEmail, customer.email(), "Customer email should match");
    }

    @Given("customer with email {word} exists and has a subscripion")
    public void givenCustomerWithEmailExistsAndHasASubscription(String email) {
        customerEmail = email;
        customerPassword = aPassword();

        customer = app.signupCustomer(customerEmail, customerPassword).expectSuccess();
        customerId = customer.id();

        List<Long> recipeIds = app.havingRecipes(3);

        String address = "123 Main St\n12345 New York\nUSA";
        app.createSubscription(customerEmail, recipeIds, address).expectSuccess();
    }

    @When("they log in using their email and password")
    public void whenTheyLogInUsingEmailAndPassword() {
        var loginResponse = app.loginCustomer(customerEmail, customerPassword);
        loginResult = loginResponse.expectSuccess();
    }

    @Then("they can access their subscription")
    public void thenTheyCanAccessTheirSubscription() {
        assertNotNull(loginResult, "Login result should not be null");
        assertNotNull(loginResult.token(), "Authentication token should not be null");

        SubscriptionResponse subscription = app.getCustomerSubscription(loginResult.token()).expectSuccess();

        assertNotNull(subscription, "Subscription should not be null");
        assertNotNull(subscription.id(), "Subscription ID should not be null");
        assertNotNull(subscription.customerId(), "Customer ID should not be null");
        assertEquals(customerId, subscription.customerId(), "Subscription should belong to the logged-in customer");
    }
}
