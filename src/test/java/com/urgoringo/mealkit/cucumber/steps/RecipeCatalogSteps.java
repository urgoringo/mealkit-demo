package com.urgoringo.mealkit.cucumber.steps;

import com.urgoringo.mealkit.cucumber.ApplicationRunner;
import com.urgoringo.mealkit.cucumber.ApplicationRunner.RecipeResponse;
import lombok.RequiredArgsConstructor;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Step definitions for recipe catalog scenarios.
 */
@RequiredArgsConstructor
public class RecipeCatalogSteps {

    private final ApplicationRunner app;
    private List<String> expectedRecipes;
    private List<RecipeResponse> recipes;

    @Before
    public void cleanupDatabase() {
        // Clean up data before each scenario to ensure test isolation
        // Must delete subscriptions first due to foreign key constraints
        app.deleteAllSubscriptions();
        app.deleteAllRecipes();
    }

    @Given("system has following recipes available")
    public void givenSystemHasRecipesAvailable(String recipeList) {
        // Parse the recipe list (format: "- Recipe One\n- Recipe Two\n- Recipe Three")
        expectedRecipes = new ArrayList<>();
        String[] lines = recipeList.split("\n");
        for (String line : lines) {
            // Remove bullet point and trim
            String recipe = line.replaceFirst("^-\\s*", "").trim();
            if (!recipe.isEmpty()) {
                expectedRecipes.add(recipe);
            }
        }

        // Create each recipe via API
        for (String recipeName : expectedRecipes) {
            app.createRecipe(recipeName).expectSuccess();
        }
    }

    @When("customer queries available recipes")
    public void whenCustomerQueriesAvailableRecipes() {
        recipes = app.getAllRecipes();
    }

    @Then("system returns these {recipeCount} recipes")
    public void thenSystemReturnsRecipes(int count) {
        assertEquals(count, recipes.size(), "Should return " + count + " recipes");

        // Verify the recipe titles match
        for (int i = 0; i < count; i++) {
            assertEquals(expectedRecipes.get(i), recipes.get(i).title(),
                    "Recipe " + (i + 1) + " title should match");
        }
    }
}
