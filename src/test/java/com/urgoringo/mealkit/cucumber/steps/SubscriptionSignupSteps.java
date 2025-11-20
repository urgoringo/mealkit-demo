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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Step definitions for subscription signup scenarios.
 */
@RequiredArgsConstructor
public class SubscriptionSignupSteps {

    private final ApplicationRunner app;
    private String customerEmail;
    private List<RecipeResponse> availableRecipes;
    private List<Long> chosenRecipeIds;
    private SubscriptionResponse subscription;
    private ApiResponse<SubscriptionResponse> response;
    private String homeAddress;
    private LocalDate today;
    private DayOfWeek deliveryDay;

    @Before
    public void cleanupDatabase() {
        // Clean up data before each scenario to ensure test isolation
        app.deleteAllSubscriptions();
        app.deleteAllRecipes();
    }

    @Given("customer has no existing subscription")
    public void givenCustomerHasNoExistingSubscription() {
        // Set a test customer email (customer will be created during subscription signup)
        customerEmail = "test-customer-" + System.currentTimeMillis() + "@example.com";
    }

    @Given("{recipeCount} recipes are available in the system")
    public void givenRecipesAreAvailable(int count) {
        availableRecipes = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            RecipeResponse recipe = app.createRecipe("Recipe " + i).expectSuccess();
            availableRecipes.add(recipe);
        }
    }

    @When("customer chooses these recipes for upcoming order")
    public void whenCustomerChoosesRecipes() {
        // Collect the IDs of all available recipes
        chosenRecipeIds = availableRecipes.stream()
                .map(RecipeResponse::id)
                .toList();

        // Create subscription with chosen recipes via API
        // Provide a default address for scenarios that don't test address rules
        String defaultAddress = "123 Main St\n12345 New York\nUSA";
        response = app.createSubscription(customerEmail, chosenRecipeIds, defaultAddress);
        subscription = response.expectSuccess();
    }

    @Then("system creates new subscription with upcoming order that contains these {recipeCount} recipes")
    public void thenSubscriptionIsCreated(int count) {
        // Verify subscription was created
        assertNotNull(subscription, "Subscription should not be null");
        assertNotNull(subscription.id(), "Subscription ID should not be null");
        assertNotNull(subscription.customerId(), "Customer ID should not be null");

        // Verify upcoming order exists
        assertNotNull(subscription.upcomingOrders(), "Upcoming orders should not be null");
        assertEquals(1, subscription.upcomingOrders().size(),
            "Should have exactly one upcoming order");

        // Verify order contains the correct recipes
        var firstOrder = subscription.upcomingOrders().get(0);
        assertNotNull(firstOrder.recipeIds(), "Order recipe IDs should not be null");
        assertEquals(count, firstOrder.recipeIds().size(),
            "Order should contain " + count + " recipes");
        assertEquals(chosenRecipeIds, firstOrder.recipeIds(),
            "Order recipes should match chosen recipes");
    }

    @Given("customer with email: {email} already exists")
    public void givenCustomerWithEmailAlreadyExists(String email) {
        customerEmail = email;
        // Create recipes and subscription to establish the customer with this email
        List<Long> recipeIds = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            RecipeResponse recipe = app.createRecipe("Recipe " + i).expectSuccess();
            recipeIds.add(recipe.id());
        }
        String defaultAddress = "123 Main St\n12345 New York\nUSA";
        app.createSubscription(customerEmail, recipeIds, defaultAddress).expectSuccess();
    }

    @When("customer tries to signup subsciption using {email}")
    public void whenCustomerTriesToSignupSubscription(String email) {
        List<Long> recipeIds = List.of();
        String defaultAddress = "123 Main St\n12345 New York\nUSA";
        response = app.createSubscription(email, recipeIds, defaultAddress);
    }

    @Then("system returns {statusCode} with validation error")
    public void thenSystemReturnsStatusWithValidationError(int statusCode) {
        assertNotNull(response, "Response should not be null");
        int actualStatusCode = response.expectError();
        assertEquals(statusCode, actualStatusCode, "Expected status code " + statusCode);
    }

    @Given("customer has selected only {recipeCount} recipes")
    public void givenCustomerHasSelectedOnlyRecipes(int count) {
        // Set a test customer email
        customerEmail = "test-customer-" + System.currentTimeMillis() + "@example.com";

        // Create the specified number of recipes
        availableRecipes = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            RecipeResponse recipe = app.createRecipe("Recipe " + i).expectSuccess();
            availableRecipes.add(recipe);
        }

        // Collect recipe IDs for the subscription
        chosenRecipeIds = availableRecipes.stream()
                .map(RecipeResponse::id)
                .toList();
    }

    @When("customer tries to sign up for subscription")
    public void whenCustomerTriesToSignUpForSubscription() {
        String defaultAddress = "123 Main St\n12345 New York\nUSA";
        response = app.createSubscription(customerEmail, chosenRecipeIds, defaultAddress);
    }

    @When("customer tries to signup without delivery address")
    public void whenCustomerTriesToSignupWithoutDeliveryAddress() {
        // Set up test customer and recipes
        customerEmail = "test-customer-" + System.currentTimeMillis() + "@example.com";

        List<Long> recipeIds = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            RecipeResponse recipe = app.createRecipe("Recipe " + i).expectSuccess();
            recipeIds.add(recipe.id());
        }

        // Try to create subscription without address (null)
        response = app.createSubscription(customerEmail, recipeIds, null);
    }

    @Given("customer home address is:")
    public void givenCustomerHomeAddressIs(String address) {
        // Set a test customer email
        customerEmail = "test-customer-" + System.currentTimeMillis() + "@example.com";

        // Store the address (will be in multiline format from the spec)
        homeAddress = address;
    }

    @When("they signup for subscription")
    public void whenTheySignupForSubscription() {
        // Create 3 recipes for the subscription (minimum required)
        availableRecipes = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            RecipeResponse recipe = app.createRecipe("Recipe " + i).expectSuccess();
            availableRecipes.add(recipe);
        }

        // Collect recipe IDs
        chosenRecipeIds = availableRecipes.stream()
                .map(RecipeResponse::id)
                .toList();

        // Create subscription with address and delivery day (if specified)
        // Use a default address if one wasn't set
        String address = homeAddress != null ? homeAddress : "123 Main St\n12345 New York\nUSA";
        response = app.createSubscription(customerEmail, chosenRecipeIds, address, deliveryDay);
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
        today = date;
    }

    @Given("customer selects {dayOfWeek} as the delivery day")
    public void givenCustomerSelectsDeliveryDay(DayOfWeek dayOfWeek) {
        // Set a test customer email
        customerEmail = "test-customer-" + System.currentTimeMillis() + "@example.com";

        // Store the selected delivery day
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
