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
        return createSubscription(customerEmail, recipeIds, deliveryAddress, (DayOfWeek) null);
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
     * Creates a new subscription via the API with a specific delivery date for the first order.
     *
     * @param customerEmail the customer email
     * @param recipeIds the list of recipe IDs for the first order
     * @param deliveryAddress the delivery address
     * @param deliveryDate the delivery date for the first order
     * @return ApiResponse containing either success with subscription or error with status code
     */
    public ApiResponse<SubscriptionResponse> createSubscription(String customerEmail, List<Long> recipeIds, String deliveryAddress, LocalDate deliveryDate) {
        CreateSubscriptionRequestWithDate request = new CreateSubscriptionRequestWithDate(customerEmail, recipeIds, deliveryAddress, deliveryDate);
        ResponseEntity<SubscriptionResponse> response = restTemplate.postForEntity(
                "/subscriptions",
                request,
                SubscriptionResponse.class
        );
        return ApiResponse.from(response);
    }

    /**
     * Processes subscription orders via the API.
     * This triggers the system to check if new orders should be added to the subscription.
     * Recipes are automatically selected randomly by the system.
     *
     * @param subscriptionId the subscription ID
     */
    public void processSubscriptionOrders(Long subscriptionId) {
        restTemplate.postForEntity(
                "/subscriptions/" + subscriptionId + "/process-orders",
                null,
                Void.class
        );
    }

    /**
     * Signs up a new customer via the API.
     *
     * @param email the customer email
     * @param password the customer password
     * @return ApiResponse containing either success with customer or error with status code
     */
    public ApiResponse<CustomerResponse> signupCustomer(String email, String password) {
        SignupCustomerRequest request = new SignupCustomerRequest(email, password);
        ResponseEntity<CustomerResponse> response = restTemplate.postForEntity(
                "/customers/signup",
                request,
                CustomerResponse.class
        );
        return ApiResponse.from(response);
    }

    /**
     * Logs in a customer via the API.
     *
     * @param email the customer email
     * @param password the customer password
     * @return ApiResponse containing either success with login result (token) or error with status code
     */
    public ApiResponse<LoginResponse> loginCustomer(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                "/customers/login",
                request,
                LoginResponse.class
        );
        return ApiResponse.from(response);
    }

    /**
     * Retrieves the authenticated customer's subscription via the API.
     * Uses Bearer token authentication.
     *
     * @param token the authentication token
     * @return ApiResponse containing either success with subscription or error with status code
     */
    public ApiResponse<SubscriptionResponse> getMySubscription(String token) {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        org.springframework.http.HttpEntity<Void> entity = new org.springframework.http.HttpEntity<>(headers);

        ResponseEntity<SubscriptionResponse> response = restTemplate.exchange(
                "/subscriptions",
                org.springframework.http.HttpMethod.GET,
                entity,
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

    /**
     * Request DTO for creating a subscription with a specific delivery date.
     */
    public record CreateSubscriptionRequestWithDate(String customerEmail, List<Long> recipeIds, String deliveryAddress, LocalDate deliveryDate) {}

    /**
     * Request DTO for customer signup.
     */
    public record SignupCustomerRequest(String email, String password) {}

    /**
     * Response DTO for customer data.
     */
    public record CustomerResponse(Long id, String email) {}

    /**
     * Request DTO for customer login.
     */
    public record LoginRequest(String email, String password) {}

    /**
     * Response DTO for login result.
     * Contains authentication token for subsequent API requests.
     */
    public record LoginResponse(String token) {}
}
