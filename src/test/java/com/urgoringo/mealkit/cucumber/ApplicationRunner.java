package com.urgoringo.mealkit.cucumber;

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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test helper class that encapsulates API access logic for Cucumber scenarios.
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
     * @return ApiResponse containing either success with recipe or error with status code
     */
    public ApiResponse<RecipeResponse> createRecipe(String title) {
        CreateRecipeRequest request = new CreateRecipeRequest(title);
        ResponseEntity<RecipeResponse> response = restTemplate.postForEntity(
                "/recipes",
                request,
                RecipeResponse.class
        );
        return ApiResponse.from(response);
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
     * @return ApiResponse containing either success with subscription or error with status code
     */
    public ApiResponse<SubscriptionResponse> createSubscription(String customerEmail, List<Long> recipeIds) {
        return createSubscription(customerEmail, recipeIds, null);
    }

    /**
     * Creates a new subscription via the API with a delivery address.
     *
     * @param customerEmail the customer email
     * @param recipeIds the list of recipe IDs for the first order
     * @param deliveryAddress the delivery address (optional)
     * @return ApiResponse containing either success with subscription or error with status code
     */
    public ApiResponse<SubscriptionResponse> createSubscription(String customerEmail, List<Long> recipeIds, String deliveryAddress) {
        return createSubscription(customerEmail, recipeIds, deliveryAddress, null);
    }

    /**
     * Creates a new subscription via the API with a delivery address and delivery day.
     *
     * @param customerEmail the customer email
     * @param recipeIds the list of recipe IDs for the first order
     * @param deliveryAddress the delivery address (optional)
     * @param deliveryDay the delivery day of the week (optional)
     * @return ApiResponse containing either success with subscription or error with status code
     */
    public ApiResponse<SubscriptionResponse> createSubscription(String customerEmail, List<Long> recipeIds, String deliveryAddress, DayOfWeek deliveryDay) {
        CreateSubscriptionRequest request = new CreateSubscriptionRequest(customerEmail, recipeIds, deliveryAddress, deliveryDay);
        ResponseEntity<SubscriptionResponse> response = restTemplate.postForEntity(
                "/subscriptions",
                request,
                SubscriptionResponse.class
        );
        return ApiResponse.from(response);
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
    public record CreateSubscriptionRequest(String customerEmail, List<Long> recipeIds, String deliveryAddress, DayOfWeek deliveryDay) {}

    /**
     * Response DTO for subscription data.
     */
    public record SubscriptionResponse(Long id, Long customerId, List<OrderResponse> upcomingOrders, String deliveryAddress) {}

    /**
     * Response DTO for order data.
     */
    public record OrderResponse(Long id, List<Long> recipeIds, LocalDate deliveryDate) {}
}
