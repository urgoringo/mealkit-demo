package com.urgoringo.mealkit.scaffolding;

import com.urgoringo.mealkit.customer.domain.Customers;
import com.urgoringo.mealkit.recipecatalog.api.RecipeController;
import com.urgoringo.mealkit.recipecatalog.api.RecipeController.CreateRecipeRequest;
import com.urgoringo.mealkit.recipecatalog.domain.RecipesCatalog;
import com.urgoringo.mealkit.subscription.application.SelectRecipesForUpcomingOrdersService;
import com.urgoringo.mealkit.subscription.domain.Subscriptions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import static com.urgoringo.mealkit.customer.api.CustomerController.*;
import static com.urgoringo.mealkit.recipecatalog.api.RecipeController.*;
import static com.urgoringo.mealkit.scaffolding.TestFactory.*;
import static com.urgoringo.mealkit.subscription.api.SubscriptionController.*;
import static com.urgoringo.mealkit.subscription.api.SubscriptionController.CreateSubscriptionRequest;
import static com.urgoringo.mealkit.subscription.api.SubscriptionController.SubscriptionResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

/**
 * Test helper class that encapsulates API access logic for Cucumber scenarios.
 * Provides high-level methods for interacting with the application API,
 * hiding low-level HTTP details from step definitions.
 */
@Component
@RequiredArgsConstructor
public class ApplicationRunner {

    private final TestRestTemplate restTemplate;
    private final RecipesCatalog recipesCatalog;
    private final Customers customers;
    private final Subscriptions subscriptions;
    private final TestClock testClock;
    private final SelectRecipesForUpcomingOrdersService selectRecipesForUpcomingOrdersService;
    @Nullable
    @Getter
    private String currentAuthToken;

    public RecipeResponse havingRecipe(String title) {
        CreateRecipeRequest request = new CreateRecipeRequest(title, List.of(), List.of());
        ResponseEntity<RecipeResponse> response = restTemplate.postForEntity(
                "/recipes",
                request,
                RecipeResponse.class
        );
        return ApiResponse.from(response).expectSuccess();
    }

    public RecipeResponse havingRecipe(TestFactory.RecipeBuilder builder) {
        var ingredientRequests = builder.ingredientsWithDetails().isEmpty()
                ? List.<RecipeController.IngredientRequest>of()
                : builder.ingredientsWithDetails().stream()
                        .map(ing -> new RecipeController.IngredientRequest(ing.name(), ing.quantity(), ing.unit()))
                        .toList();
        
        CreateRecipeRequest request = new CreateRecipeRequest(
                builder.title(), 
                builder.instructions(),
                ingredientRequests
        );
        ResponseEntity<RecipeResponse> response = restTemplate.postForEntity(
                "/recipes",
                request,
                RecipeResponse.class
        );
        return ApiResponse.from(response).expectSuccess();
    }

    public RecipeResponse getRecipe(Long id) {
        ResponseEntity<RecipeResponse> response = restTemplate.getForEntity(
                "/recipes/" + id,
                RecipeResponse.class
        );
        return ApiResponse.from(response).expectSuccess();
    }

    public List<RecipeResponse> getAllRecipes() {
        ResponseEntity<List<RecipeResponse>> response = restTemplate.exchange(
                "/recipes",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "Failed to get recipes");
        assertNotNull(response.getBody(), "Recipe list should not be null");
        return response.getBody();
    }

    public void deleteAllRecipes() {
        recipesCatalog.deleteAll();
    }

    public void setup() {
        reset();
        IntStream.rangeClosed(1, 10).forEach(_ -> havingRecipe(aRecipe()));
    }

    public void updateUpcomingOrderRecipes(String authToken, List<Long> recipeIds) {
        UpdateUpcomingOrderRecipesRequest request = new UpdateUpcomingOrderRecipesRequest(recipeIds);
        var headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);
        var entity = new HttpEntity<>(request, headers);

        ResponseEntity<SubscriptionResponse> response = restTemplate.exchange(
                "/subscriptions/upcoming-order/recipes",
                PUT,
                entity,
                SubscriptionResponse.class
        );
        ApiResponse.from(response).expectSuccess();
    }


    public ApiResponse<@NotNull SubscriptionResponse> create(String token, SubscriptionBuilder request) {
        CreateSubscriptionRequest apiRequest = new CreateSubscriptionRequest(
                request.recipeIds(),
                request.deliveryAddress(),
                request.deliveryDay()
        );

        var headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        ResponseEntity<SubscriptionResponse> response = restTemplate.exchange(
                "/subscriptions",
                POST,
                new HttpEntity<>(apiRequest, headers),
                SubscriptionResponse.class
        );
        return ApiResponse.from(response);
    }

    public void processSubscriptionOrders() {
        // This method is kept for backward compatibility but now triggers processing for all subscriptions
        // In production, this is handled by the scheduled job
        selectRecipesForUpcomingOrdersService.execute();
    }

    public ApiResponse<@NotNull SignupResponse> signupCustomer() {
        return signupCustomer(anEmail(), aPassword());
    }

    public ApiResponse<@NotNull SignupResponse> signupCustomer(String email, String password) {
        SignupRequest request = new SignupRequest(email, password);
        ResponseEntity<SignupResponse> response = restTemplate.postForEntity(
                "/customers/signup",
                request,
                SignupResponse.class
        );
        return ApiResponse.from(response);
    }

    public ApiResponse<@NotNull LoginResponse> loginCustomer(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                "/customers/login",
                request,
                LoginResponse.class
        );
        return ApiResponse.from(response);
    }

    public ApiResponse<@NotNull SubscriptionResponse> getCustomerSubscription(String token) {
        var headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        var entity = new org.springframework.http.HttpEntity<Void>(headers);

        ResponseEntity<SubscriptionResponse> response = restTemplate.exchange(
                "/subscriptions",
                org.springframework.http.HttpMethod.GET,
                entity,
                SubscriptionResponse.class
        );
        return ApiResponse.from(response);
    }

    public void deleteAllSubscriptions() {
        subscriptions.deleteAll();
        customers.deleteAll();
    }

    public ApplicationRunner freezeTimeOn(LocalDate date) {
        testClock.freezeTime(date);
        return this;
    }

    private void reset() {
        deleteAllSubscriptions();
        deleteAllRecipes();
        testClock.reset();
    }

    public void havingRecipes(List<String> names) {
        names.forEach(this::havingRecipe);
    }

    public List<Long> getRecipes(int count) {
        List<RecipeResponse> recipes = getAllRecipes();
        return recipes.subList(0, count).stream()
                .map(RecipeResponse::id)
                .toList();
    }

    public CustomerSetup havingCustomer() {
        CustomerSetup customer = new CustomerSetup(this);
        currentAuthToken = customer.getAuthToken();
        return customer;
    }
}
