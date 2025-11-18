package com.urgoringo.mealkit.jbehave;

import com.urgoringo.mealkit.persistence.CustomerRepository;
import com.urgoringo.mealkit.persistence.RecipeRepository;
import com.urgoringo.mealkit.persistence.SubscriptionRepository;
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
    private final CustomerRepository customerRepository;
    private final SubscriptionRepository subscriptionRepository;

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
     * Creates a new subscription via the API.
     *
     * @param customerEmail the customer email
     * @param recipeIds the list of recipe IDs for the first order
     * @return the created subscription response
     */
    public SubscriptionResponse createSubscription(String customerEmail, List<Long> recipeIds) {
        CreateSubscriptionRequest request = new CreateSubscriptionRequest(customerEmail, recipeIds);
        ResponseEntity<SubscriptionResponse> response = restTemplate.postForEntity(
                "/subscriptions",
                request,
                SubscriptionResponse.class
        );
        assertEquals(HttpStatus.CREATED, response.getStatusCode(),
                "Failed to create subscription for customer: " + customerEmail);
        assertNotNull(response.getBody(), "Subscription response body should not be null");
        return response.getBody();
    }

    /**
     * Deletes all subscriptions and customers from the database.
     * Used for test cleanup to ensure scenario isolation.
     */
    public void deleteAllSubscriptions() {
        subscriptionRepository.deleteAll();
        customerRepository.deleteAll();
    }

    /**
     * Request DTO for creating a recipe.
     */
    public record CreateRecipeRequest(String title) {}

    /**
     * Response DTO for recipe data.
     */
    public record RecipeResponse(Long id, String title) {}

    /**
     * Request DTO for creating a subscription.
     */
    public record CreateSubscriptionRequest(String customerEmail, List<Long> recipeIds) {}

    /**
     * Response DTO for subscription data.
     */
    public record SubscriptionResponse(Long id, Long customerId, List<OrderResponse> upcomingOrders) {}

    /**
     * Response DTO for order data.
     */
    public record OrderResponse(Long id, List<Long> recipeIds) {}
}
