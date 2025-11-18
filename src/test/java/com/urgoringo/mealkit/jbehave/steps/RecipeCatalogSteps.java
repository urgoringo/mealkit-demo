package com.urgoringo.mealkit.jbehave.steps;

import lombok.RequiredArgsConstructor;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Step definitions for recipe catalog scenarios.
 */
@Component
@RequiredArgsConstructor
public class RecipeCatalogSteps {

    private final TestRestTemplate restTemplate;
    private List<String> expectedRecipes;
    private ResponseEntity<List<RecipeResponse>> response;

    @Given("system has following recipes available $recipeList")
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

        // Create each recipe via REST API
        for (String recipeName : expectedRecipes) {
            CreateRecipeRequest request = new CreateRecipeRequest(recipeName);
            ResponseEntity<RecipeResponse> createResponse = restTemplate.postForEntity(
                    "/recipes",
                    request,
                    RecipeResponse.class
            );
            assertEquals(HttpStatus.CREATED, createResponse.getStatusCode(),
                    "Failed to create recipe: " + recipeName);
        }
    }

    @When("customer queries available recipes")
    public void whenCustomerQueriesAvailableRecipes() {
        response = restTemplate.exchange(
                "/recipes",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<RecipeResponse>>() {}
        );
    }

    @Then("system returns these $count recipes")
    public void thenSystemReturnsRecipes(int count) {
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status should be OK");

        List<RecipeResponse> recipes = response.getBody();
        assertNotNull(recipes, "Recipe list should not be null");
        assertEquals(count, recipes.size(), "Should return " + count + " recipes");

        // Verify the recipe titles match
        for (int i = 0; i < count; i++) {
            assertEquals(expectedRecipes.get(i), recipes.get(i).title(),
                    "Recipe " + (i + 1) + " title should match");
        }
    }

    /**
     * Request DTO for creating a recipe.
     */
    public record CreateRecipeRequest(String title) {}

    /**
     * Simple record to represent recipe response from API.
     */
    public record RecipeResponse(Long id, String title) {}
}
