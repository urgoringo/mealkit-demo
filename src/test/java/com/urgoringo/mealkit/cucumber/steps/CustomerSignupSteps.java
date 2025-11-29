package com.urgoringo.mealkit.cucumber.steps;

import com.urgoringo.mealkit.cucumber.ApiResponse;
import com.urgoringo.mealkit.cucumber.ApplicationRunner;
import com.urgoringo.mealkit.cucumber.ApplicationRunner.SignupResponse;
import com.urgoringo.mealkit.cucumber.ApplicationRunner.LoginResponse;
import com.urgoringo.mealkit.cucumber.ApplicationRunner.SubscriptionResponse;
import lombok.RequiredArgsConstructor;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.IntStream;

import static com.urgoringo.mealkit.cucumber.ApplicationRunner.SubscriptionRequest.aSubscription;
import static com.urgoringo.mealkit.cucumber.scaffolding.TestFactory.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Step definitions for customer authentication scenarios.
 */
@RequiredArgsConstructor
public class CustomerSignupSteps {

    private final ApplicationRunner app;
    private final LastResponseState responseState;
    private String customerEmail;
    private String customerPassword;
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
        responseState.setLastResponse(signupResponse);
    }

    @Then("system creates new customer with used credentials")
    public void thenSystemCreatesNewCustomerWithUsedCredentials() {
        SignupResponse signupResponse = responseState.getLastResponseExpectSuccess();
        assertNotNull(signupResponse.id(), "Customer ID should not be null");
        assertEquals(customerEmail, signupResponse.email(), "Customer email should match");
    }

    @Given("customer with email {word} exists and has a subscripion")
    public void givenCustomerWithEmailExistsAndHasASubscription(String email) {
        customerEmail = email;
        customerPassword = aPassword();

        var customer = app.signupCustomer(customerEmail, customerPassword).expectSuccess();
        customerId = customer.id();

        List<Long> recipeIds = app.havingRecipes(3);

        app.signup(customer.token(), aSubscription(recipeIds)).expectSuccess();
    }

    @Given("customer with email: {email} already exists")
    public void givenCustomerWithEmailAlreadyExists(String email) {
        customerEmail = email;
        customerPassword = aPassword();
        app.signupCustomer(customerEmail, customerPassword).expectSuccess();
    }

    @When("customer tries to signup using {email}")
    public void whenCustomerTriesToSignup(String email) {
        responseState.setLastResponse(app.signupCustomer(email, aPassword()));
    }

    @When("they log in using their email and password")
    public void whenTheyLogInUsingEmailAndPassword() {
        var loginResponse = app.loginCustomer(customerEmail, customerPassword);
        responseState.setLastResponse(loginResponse);
    }

    @Then("they can access their subscription")
    public void thenTheyCanAccessTheirSubscription() {
        LoginResponse loginResult = responseState.getLastResponseExpectSuccess();

        SubscriptionResponse subscription = app.getCustomerSubscription(loginResult.token()).expectSuccess();

        assertNotNull(subscription, "Subscription should not be null");
        assertNotNull(subscription.id(), "Subscription ID should not be null");
        assertNotNull(subscription.customerId(), "Customer ID should not be null");
        assertEquals(customerId, subscription.customerId(), "Subscription should belong to the logged-in customer");
    }
}
