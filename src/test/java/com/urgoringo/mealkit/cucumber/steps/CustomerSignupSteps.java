package com.urgoringo.mealkit.cucumber.steps;

import com.urgoringo.mealkit.cucumber.ApplicationRunner;
import com.urgoringo.mealkit.cucumber.ApplicationRunner.SignupResponse;
import com.urgoringo.mealkit.cucumber.ApplicationRunner.SubscriptionResponse;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.urgoringo.mealkit.cucumber.ApplicationRunner.SubscriptionRequest.aSubscription;
import static com.urgoringo.mealkit.cucumber.scaffolding.TestFactory.aPassword;
import static com.urgoringo.mealkit.cucumber.scaffolding.TestFactory.anEmail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RequiredArgsConstructor
public class CustomerSignupSteps {

    private final ApplicationRunner app;
    private final LastResponseState responseState;
    private String givenEmail;
    private String givenPassword;
    private Long customerId;
    private SignupResponse signupResponse;
    private String accessToken;

    @Before
    public void cleanupDatabase() {
        app.deleteAllSubscriptions();
        app.deleteAllRecipes();
    }

    @When("user signs up using their email and password")
    public void whenUserSignsUpUsingEmailAndPassword() {
        givenEmail = anEmail();
        givenPassword = aPassword();

        signupResponse = app.signupCustomer(givenEmail, givenPassword).expectSuccess();
    }

    @Then("system creates new customer with used credentials")
    public void thenSystemCreatesNewCustomerWithUsedCredentials() {
        assertNotNull(signupResponse.id(), "Customer ID should not be null");
        assertEquals(givenEmail, signupResponse.email(), "Customer email should match");
    }

    @Given("customer with email {word} exists and has a subscripion")
    public void givenCustomerWithEmailExistsAndHasASubscription(String email) {
        givenEmail = email;
        givenPassword = aPassword();

        var customer = app.signupCustomer(givenEmail, givenPassword).expectSuccess();
        customerId = customer.id();

        List<Long> recipeIds = app.havingRecipes(3);

        app.create(customer.token(), aSubscription(recipeIds)).expectSuccess();
    }

    @Given("customer with email: {email} already exists")
    public void givenCustomerWithEmailAlreadyExists(String email) {
        givenEmail = email;
        givenPassword = aPassword();
        app.signupCustomer(givenEmail, givenPassword).expectSuccess();
    }

    @When("customer tries to signup using {email}")
    public void whenCustomerTriesToSignup(String email) {
        responseState.setLastResponse(app.signupCustomer(email, aPassword()));
    }

    @When("they log in using their email and password")
    public void whenTheyLogInUsingEmailAndPassword() {
        var loginResponse = app.loginCustomer(givenEmail, givenPassword);
        accessToken = loginResponse.expectSuccess().token();
    }

    @Then("they can access their subscription")
    public void thenTheyCanAccessTheirSubscription() {
        SubscriptionResponse subscription = app.getCustomerSubscription(accessToken).expectSuccess();

        assertNotNull(subscription, "Subscription should not be null");
        assertNotNull(subscription.id(), "Subscription ID should not be null");
        assertNotNull(subscription.customerId(), "Customer ID should not be null");
        assertEquals(customerId, subscription.customerId(), "Subscription should belong to the logged-in customer");
    }
}
