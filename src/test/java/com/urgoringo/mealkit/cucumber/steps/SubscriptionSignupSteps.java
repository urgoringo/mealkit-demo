package com.urgoringo.mealkit.cucumber.steps;

import com.urgoringo.mealkit.cucumber.ApiResponse;
import com.urgoringo.mealkit.cucumber.ApplicationRunner;
import com.urgoringo.mealkit.cucumber.ApplicationRunner.RecipeResponse;
import com.urgoringo.mealkit.cucumber.ApplicationRunner.SubscriptionResponse;
import lombok.RequiredArgsConstructor;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.urgoringo.mealkit.cucumber.ApplicationRunner.SubscriptionRequest.aSubscription;
import static com.urgoringo.mealkit.cucumber.scaffolding.TestFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Step definitions for subscription signup scenarios.
 */
@RequiredArgsConstructor
public class SubscriptionSignupSteps {

    private final ApplicationRunner app;
    private final LastResponseState responseState;
    private String customerEmail;
    private String customerPassword;
    private String authToken;
    private List<RecipeResponse> availableRecipes;
    private List<Long> chosenRecipeIds;
    private SubscriptionResponse subscription;
    private String homeAddress;
    private DayOfWeek deliveryDay;

    @Before
    public void cleanupDatabase() {
        app.deleteAllSubscriptions();
        app.deleteAllRecipes();
        app.reset();
    }

    @Given("customer has no existing subscription")
    public void givenCustomerHasNoExistingSubscription() {
        customerEmail = aCustomerEmail();
        customerPassword = aPassword();
        authToken = app.signupCustomer(customerEmail, customerPassword).expectSuccess().token();
    }

    @Given("{recipeCount} recipes are available in the system")
    public void givenRecipesAreAvailable(int count) {
        availableRecipes = new ArrayList<>();
        IntStream.rangeClosed(1, count)
                .mapToObj(i -> app.havingRecipe("Recipe " + i))
                .forEach(recipe -> availableRecipes.add(recipe));
    }

    @When("customer chooses these recipes for upcoming order")
    public void whenCustomerChoosesRecipes() {
        chosenRecipeIds = availableRecipes.stream()
                .map(RecipeResponse::id)
                .toList();

        ApiResponse<@NotNull SubscriptionResponse> response = app.signup(authToken, aSubscription(chosenRecipeIds));
        subscription = response.expectSuccess();
    }

    @Then("system creates new subscription with upcoming order that contains these {recipeCount} recipes")
    public void thenSubscriptionIsCreated(int count) {
        assertNotNull(subscription, "Subscription should not be null");
        assertNotNull(subscription.id(), "Subscription ID should not be null");
        assertNotNull(subscription.customerId(), "Customer ID should not be null");

        assertNotNull(subscription.upcomingOrders(), "Upcoming orders should not be null");
        assertEquals(1, subscription.upcomingOrders().size(),
                "Should have exactly one upcoming order");

        var firstOrder = subscription.upcomingOrders().get(0);
        assertNotNull(firstOrder.recipeIds(), "Order recipe IDs should not be null");
        assertEquals(count, firstOrder.recipeIds().size(),
                "Order should contain " + count + " recipes");

        assertEquals(chosenRecipeIds, firstOrder.recipeIds(),
                "Order recipes should match chosen recipes");

        IntStream.range(0, chosenRecipeIds.size()).forEach(i -> {
            Long expectedRecipeId = chosenRecipeIds.get(i);
            Long actualRecipeId = firstOrder.recipeIds().get(i);
            assertEquals(expectedRecipeId, actualRecipeId,
                    "Recipe at position " + i + " should match. Expected: " +
                            availableRecipes.get(i).title() + " (ID: " + expectedRecipeId + ")");
        });
    }

//    @When("customer tries to signup subsciption using {email}")
//    public void whenCustomerTriesToSignupSubscription(String email) {
//        // Customer already exists from the Given step, try to create subscription with invalid token
//        String invalidToken = "invalid-token";
//        List<Long> recipeIds = app.havingRecipes(3);
//        response = app.signup(invalidToken, aSubscription(recipeIds));
//    }

    @Then("system returns {statusCode} with validation error")
    public void thenSystemReturnsStatusWithValidationError(int statusCode) {
        assertNotNull(responseState.getLastResponse(), "Response should not be null");
        int actualStatusCode = responseState.getLastResponse().expectError();
        assertEquals(statusCode, actualStatusCode, "Expected status code " + statusCode);
    }

    @Given("customer has selected only {recipeCount} recipes")
    public void givenCustomerHasSelectedOnlyRecipes(int count) {
        customerEmail = aCustomerEmail();
        customerPassword = aPassword();
        authToken = app.signupCustomer(customerEmail, customerPassword).expectSuccess().token();

        availableRecipes = new ArrayList<>();
        IntStream.rangeClosed(1, count)
                .mapToObj(i -> app.havingRecipe("Recipe " + i))
                .forEach(recipe -> availableRecipes.add(recipe));

        chosenRecipeIds = availableRecipes.stream()
                .map(RecipeResponse::id)
                .toList();
    }

    @When("customer tries to sign up for subscription")
    public void whenCustomerTriesToSignUpForSubscription() {
        responseState.setLastResponse(app.signup(authToken, aSubscription(chosenRecipeIds)));
    }

    @When("customer tries to signup without delivery address")
    public void whenCustomerTriesToSignupWithoutDeliveryAddress() {
        customerEmail = aCustomerEmail();
        customerPassword = aPassword();
        authToken = app.signupCustomer(customerEmail, customerPassword).expectSuccess().token();

        List<Long> recipeIds = app.havingRecipes(3);

        responseState.setLastResponse(app.signup(
                authToken,
                aSubscription(recipeIds).withDeliveryAddress(null)
        ));
    }

    @Given("customer home address is:")
    public void givenCustomerHomeAddressIs(String address) {
        customerEmail = aCustomerEmail();
        customerPassword = aPassword();
        authToken = app.signupCustomer(customerEmail, customerPassword).expectSuccess().token();

        homeAddress = address;
    }

    @When("they signup for subscription")
    public void whenTheySignupForSubscription() {
        chosenRecipeIds = app.havingRecipes(3);

        var request = aSubscription(chosenRecipeIds);

        if (homeAddress != null) {
            request = request.withDeliveryAddress(homeAddress);
        }

        if (deliveryDay != null) {
            request = request.withDeliveryDay(deliveryDay);
        }

        var response = app.signup(authToken, request);
        subscription = response.expectSuccess();
    }

    @Then("subscription has customer's home address as delivery address")
    public void thenSubscriptionHasCustomerHomeAddressAsDeliveryAddress() {
        assertNotNull(subscription, "Subscription should not be null");
        assertNotNull(subscription.deliveryAddress(), "Delivery address should not be null");
        assertEquals(homeAddress, subscription.deliveryAddress(),
                "Delivery address should match customer's home address");
    }

    @Given("today is {date}")
    public void givenTodayIs(LocalDate date) {
        app.freezeTimeOn(date);
    }

    @Given("customer selects {dayOfWeek} as the delivery day")
    public void givenCustomerSelectsDeliveryDay(DayOfWeek dayOfWeek) {
        // Set a test customer email
        customerEmail = aCustomerEmail();
        customerPassword = aPassword();
        authToken = app.signupCustomer(customerEmail, customerPassword).expectSuccess().token();

        deliveryDay = dayOfWeek;
    }

    @Then("first order will be delivered on {date}")
    public void thenFirstOrderWillBeDeliveredOn(LocalDate expectedDeliveryDate) {
        // Verify subscription has upcoming orders
        assertNotNull(subscription, "Subscription should not be null");
        assertNotNull(subscription.upcomingOrders(), "Upcoming orders should not be null");
        assertEquals(1, subscription.upcomingOrders().size(),
                "Should have exactly one upcoming order");

        // Verify first order has the expected delivery date
        var firstOrder = subscription.upcomingOrders().get(0);
        assertNotNull(firstOrder.deliveryDate(), "Delivery date should not be null");
        assertEquals(expectedDeliveryDate, firstOrder.deliveryDate(),
                "First order delivery date should be " + expectedDeliveryDate);
    }
}
