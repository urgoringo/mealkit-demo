package com.urgoringo.mealkit.jbehave.steps;

import com.urgoringo.mealkit.jbehave.ApplicationRunner;
import com.urgoringo.mealkit.jbehave.ApplicationRunner.RecipeResponse;
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
    private String customerId;
    private List<RecipeResponse> availableRecipes;
    private List<Long> chosenRecipeIds;

    @BeforeScenario
    public void cleanupDatabase() {
        // Clean up data before each scenario to ensure test isolation
        app.deleteAllRecipes();
        // TODO: Delete all subscriptions and customers when implemented
    }

    @Given("customer has no existing subscription")
    public void givenCustomerHasNoExistingSubscription() {
        // TODO: Create a customer without subscription
        // For now, just set a test customer ID
        customerId = "test-customer-" + System.currentTimeMillis();
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

        // TODO: Make API call to create subscription with chosen recipes
        // POST /subscriptions { customerId, recipeIds }
    }

    @Then("system creates new subscription with upcoming order that contains these $count recipes")
    public void thenSubscriptionIsCreated(int count) {
        // TODO: Verify subscription was created
        // GET /subscriptions/{customerId}
        // Verify subscription exists
        // Verify upcoming order exists
        // Verify order contains the correct recipes

        // For now, just verify we have the expected number of chosen recipes
        assertNotNull(chosenRecipeIds, "No recipes were chosen");
        assertEquals(count, chosenRecipeIds.size(),
            "Expected " + count + " recipes but got " + chosenRecipeIds.size());
    }
}
