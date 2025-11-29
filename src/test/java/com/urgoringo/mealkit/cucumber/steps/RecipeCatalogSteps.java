package com.urgoringo.mealkit.cucumber.steps;

import com.urgoringo.mealkit.cucumber.ApplicationRunner;
import com.urgoringo.mealkit.cucumber.ApplicationRunner.RecipeResponse;
import lombok.RequiredArgsConstructor;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
public class RecipeCatalogSteps {

    private final ApplicationRunner app;
    private List<String> expectedRecipes;
    private List<RecipeResponse> recipes;

    @Before
    public void cleanupDatabase() {
        app.deleteAllSubscriptions();
        app.deleteAllRecipes();
    }

    @Given("system has following recipes available")
    public void givenSystemHasRecipesAvailable(String recipeList) {
        expectedRecipes = new ArrayList<>();
        expectedRecipes = Arrays.stream(recipeList.split("\n"))
                .map(line -> line.replaceFirst("^-\\s*", "").trim())
                .toList();
        app.havingRecipes(expectedRecipes);
    }

    @When("customer queries available recipes")
    public void whenCustomerQueriesAvailableRecipes() {
        recipes = app.getAllRecipes();
    }

    @Then("system returns these {recipeCount} recipes")
    public void thenSystemReturnsRecipes(int count) {
        assertEquals(count, recipes.size(), "Should return " + count + " recipes");

        IntStream.range(0, count).forEach(i -> assertEquals(expectedRecipes.get(i), recipes.get(i).title(),
                "Recipe " + (i + 1) + " title should match"));
    }
}
