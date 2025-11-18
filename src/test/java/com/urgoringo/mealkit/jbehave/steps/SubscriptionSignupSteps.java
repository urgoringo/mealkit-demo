package com.urgoringo.mealkit.jbehave.steps;

import com.urgoringo.mealkit.jbehave.ApiResponse;
import com.urgoringo.mealkit.jbehave.ApplicationRunner;
import com.urgoringo.mealkit.jbehave.ApplicationRunner.RecipeResponse;
import com.urgoringo.mealkit.jbehave.ApplicationRunner.SubscriptionResponse;
import lombok.RequiredArgsConstructor;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Step definitions for subscription signup scenarios.
 */
@Component
@RequiredArgsConstructor
public class SubscriptionSignupSteps {

    private final ApplicationRunner app;
    private String customerEmail;
    private List<RecipeResponse> availableRecipes;
    private List<Long> chosenRecipeIds;
    private SubscriptionResponse subscription;
    private ApiResponse<SubscriptionResponse> response;

    @BeforeScenario
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

    @Given("$count recipes are available in the system")
    public void givenRecipesAreAvailable(int count) {
        availableRecipes = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            RecipeResponse recipe = app.createRecipe("Recipe " + i);
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
        response = app.createSubscription(customerEmail, chosenRecipeIds);
        subscription = response.expectSuccess();
    }

    @Then("system creates new subscription with upcoming order that contains these $count recipes")
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

    @Given("customer with email: $email already exists")
    public void givenCustomerWithEmailAlreadyExists(String email) {
        customerEmail = email;
        // Create a subscription to establish the customer with this email
        List<Long> dummyRecipeIds = List.of();
        app.createSubscription(customerEmail, dummyRecipeIds).expectSuccess();
    }

    @When("customer tries to signup subsciption using $email")
    public void whenCustomerTriesToSignupSubscription(String email) {
        List<Long> recipeIds = List.of();
        response = app.createSubscription(email, recipeIds);
    }

    @Then("system returns $statusCode with validation error")
    public void thenSystemReturnsStatusWithValidationError(int statusCode) {
        assertNotNull(response, "Response should not be null");
        int actualStatusCode = response.expectError();
        assertEquals(statusCode, actualStatusCode, "Expected status code " + statusCode);
    }
}
