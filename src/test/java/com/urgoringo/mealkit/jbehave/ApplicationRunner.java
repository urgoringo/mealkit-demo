package com.urgoringo.mealkit.jbehave;

import com.urgoringo.mealkit.persistence.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test helper class that encapsulates API access logic for JBehave scenarios.
 * Provides high-level methods for interacting with the application API,
 * hiding low-level HTTP details from step definitions.
 */
@Component
@RequiredArgsConstructor
public class ApplicationRunner {

    private final TestRestTemplate restTemplate;
    private final RecipeRepository recipeRepository;

    /**
     * Creates a new recipe via the API.
     *
     * @param title the recipe title
     * @return the created recipe response
     */
    public RecipeResponse createRecipe(String title) {
        CreateRecipeRequest request = new CreateRecipeRequest(title);
        ResponseEntity<RecipeResponse> response = restTemplate.postForEntity(
                "/recipes",
                request,
                RecipeResponse.class
        );
        assertEquals(HttpStatus.CREATED, response.getStatusCode(),
                "Failed to create recipe: " + title);
        assertNotNull(response.getBody(), "Recipe response body should not be null");
        return response.getBody();
    }

    /**
     * Retrieves all recipes via the API.
     *
     * @return list of all recipes
     */
    public List<RecipeResponse> getAllRecipes() {
        ResponseEntity<List<RecipeResponse>> response = restTemplate.exchange(
                "/recipes",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<RecipeResponse>>() {}
        );
        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "Failed to get recipes");
        assertNotNull(response.getBody(), "Recipe list should not be null");
        return response.getBody();
    }

    /**
     * Deletes all recipes from the database.
     * Used for test cleanup to ensure scenario isolation.
     */
    public void deleteAllRecipes() {
        recipeRepository.deleteAll();
    }

    /**
     * Request DTO for creating a recipe.
     */
    public record CreateRecipeRequest(String title) {}

    /**
     * Response DTO for recipe data.
     */
    public record RecipeResponse(Long id, String title) {}
}
