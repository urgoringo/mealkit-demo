package com.urgoringo.mealkit.scaffolding;

import com.urgoringo.mealkit.customer.domain.Customers;
import com.urgoringo.mealkit.recipecatalog.api.IngredientController.CreateIngredientRequest;
import com.urgoringo.mealkit.recipecatalog.api.RecipeController.CreateRecipeRequest;
import com.urgoringo.mealkit.recipecatalog.api.RecipeController.RecipeIngredientRequest;
import com.urgoringo.mealkit.recipecatalog.api.RecipeController.RecipeResponse;
import com.urgoringo.mealkit.recipecatalog.domain.IngredientsCatalog;
import com.urgoringo.mealkit.recipecatalog.domain.RecipesCatalog;
import com.urgoringo.mealkit.subscription.application.SelectRecipesForUpcomingOrdersService;
import com.urgoringo.mealkit.subscription.domain.Subscriptions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import static com.urgoringo.mealkit.customer.api.CustomerController.*;
import static com.urgoringo.mealkit.recipecatalog.api.IngredientController.IngredientResponse;
import static com.urgoringo.mealkit.scaffolding.TestFactory.*;
import static com.urgoringo.mealkit.subscription.api.SubscriptionController.*;
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

    private final RestClient.Builder restClientBuilder;
    private final RecipesCatalog recipesCatalog;
    private final IngredientsCatalog ingredientsCatalog;
    private final Customers customers;
    private final Subscriptions subscriptions;
    private final TestClock testClock;
    private final SelectRecipesForUpcomingOrdersService selectRecipesForUpcomingOrdersService;
    private final BackofficeApplicationRunner backofficeRunner;
    @Nullable
    @Getter
    private String currentAuthToken;
    private RestClient restClient;

    public RecipeResponse havingRecipe(String title) {
        return havingRecipe(aRecipe().withTitle(title));
    }

    public RecipeResponse havingRecipe(TestFactory.RecipeBuilder builder) {
        var ingredientRequests = builder.ingredients().stream()
            .map(ingredient -> {
                long ingredientId = findOrCreateIngredient(ingredient.name());
                return new RecipeIngredientRequest(ingredientId, ingredient.quantity(), ingredient.unit());
            }).toList();

        CreateRecipeRequest request = new CreateRecipeRequest(
            builder.title(),
            builder.instructions(),
            ingredientRequests
        );
        ResponseEntity<RecipeResponse> response = restClient.post()
            .uri("/recipes")
            .body(request)
            .retrieve()
            .toEntity(RecipeResponse.class);
        return ApiResponse.from(response).expectSuccess();
    }

    private long findOrCreateIngredient(String ingredientName) {
        ApiResponse<IngredientResponse> response = findIngredient(ingredientName);
        if (response.isSuccess()) {
            return response.expectSuccess().id();
        }
        IngredientResponse createdIngredient = havingIngredient(ingredientName);
        return createdIngredient.id();
    }

    public RecipeResponse getRecipe(Long id) {
        ResponseEntity<RecipeResponse> response = restClient.get()
            .uri("/recipes/" + id)
            .retrieve()
            .toEntity(RecipeResponse.class);
        return ApiResponse.from(response).expectSuccess();
    }

    public List<RecipeResponse> getAllRecipes() {
        ResponseEntity<List<RecipeResponse>> response = restClient.get()
            .uri("/recipes")
            .retrieve()
            .toEntity(new ParameterizedTypeReference<>() {
            });
        assertEquals(HttpStatus.OK, response.getStatusCode(),
            "Failed to get recipes");
        assertNotNull(response.getBody(), "Recipe list should not be null");
        return response.getBody();
    }

    public void deleteAllRecipes() {
        recipesCatalog.deleteAll();
        ingredientsCatalog.deleteAll();
    }

    public void start(int port) {
        this.restClient = restClientBuilder
            .baseUrl("http://localhost:" + port)
            .build();
        backofficeRunner.start(port);
        reset();
        IntStream.rangeClosed(1, 10).forEach(_ -> havingRecipe(aRecipe()));
    }

    public void updateUpcomingOrderRecipes(List<Long> recipeIds, String authToken) {
        UpdateUpcomingOrderRecipesRequest request = new UpdateUpcomingOrderRecipesRequest(recipeIds);
        ResponseEntity<SubscriptionResponse> response = restClient.put()
            .uri("/subscriptions/upcoming-order/recipes")
            .header("Authorization", "Bearer " + authToken)
            .body(request)
            .retrieve()
            .toEntity(SubscriptionResponse.class);
        ApiResponse.from(response).expectSuccess();
    }


    public ApiResponse<SubscriptionResponse> create(SubscriptionBuilder request, String token) {
        CreateSubscriptionRequest apiRequest = new CreateSubscriptionRequest(
            request.recipeIds(),
            request.deliveryAddress(),
            request.deliveryDay()
        );

        ResponseEntity<SubscriptionResponse> response = restClient.post()
            .uri("/subscriptions")
            .header("Authorization", "Bearer " + token)
            .body(apiRequest)
            .retrieve()
            .toEntity(SubscriptionResponse.class);
        return ApiResponse.from(response);
    }

    public void processSubscriptionOrders() {
        selectRecipesForUpcomingOrdersService.execute();
    }

    public ApiResponse<@NotNull SignupResponse> signupCustomer() {
        return signupCustomer(anEmail(), aPassword());
    }

    public ApiResponse<@NotNull SignupResponse> signupCustomer(String email, String password) {
        SignupRequest request = new SignupRequest(email, password);
        ResponseEntity<SignupResponse> response = restClient.post()
            .uri("/customers/signup")
            .body(request)
            .retrieve()
            .toEntity(SignupResponse.class);
        return ApiResponse.from(response);
    }

    public ApiResponse<@NotNull LoginResponse> loginCustomer(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        ResponseEntity<LoginResponse> response = restClient.post()
            .uri("/customers/login")
            .body(request)
            .retrieve()
            .toEntity(LoginResponse.class);
        return ApiResponse.from(response);
    }

    public ApiResponse<@NotNull SubscriptionResponse> getCustomerSubscription(String token) {
        ResponseEntity<SubscriptionResponse> response = restClient.get()
            .uri("/subscriptions")
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .toEntity(SubscriptionResponse.class);
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
        backofficeRunner.reset();
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

    public IngredientResponse havingIngredient(String name) {
        CreateIngredientRequest request = new CreateIngredientRequest(name);
        ResponseEntity<IngredientResponse> response = restClient.post()
            .uri("/ingredients")
            .body(request)
            .retrieve()
            .toEntity(IngredientResponse.class);
        return ApiResponse.from(response).expectSuccess();
    }

    public ApiResponse<IngredientResponse> findIngredient(String name) {
        ResponseEntity<IngredientResponse> response = restClient.get()
            .uri("/ingredients?name=" + name)
            .retrieve()
            .toEntity(IngredientResponse.class);
        return ApiResponse.from(response);
    }

    public BackofficeApplicationRunner backoffice() {
        return backofficeRunner;
    }

    public ApiResponse<Void> tryDeliverOrderAsCustomer(Long orderId, String customerToken) {
        ResponseEntity<Void> response = restClient.post()
            .uri("/orders/{orderId}/delivered", orderId)
            .header("Authorization", "Bearer " + customerToken)
            .retrieve()
            .toEntity(Void.class);
        return ApiResponse.from(response);
    }

}
