package com.urgoringo.mealkit.cucumber;

import com.urgoringo.mealkit.cucumber.scaffolding.TestClock;
import com.urgoringo.mealkit.customer.persistence.CustomerJpaRepository;
import com.urgoringo.mealkit.recipecatalog.persistence.RecipeJpaRepository;
import com.urgoringo.mealkit.subscription.persistence.SubscriptionJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import static com.urgoringo.mealkit.cucumber.scaffolding.TestFactory.anAddress;
import static java.time.DayOfWeek.WEDNESDAY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpMethod.POST;

/**
 * Test helper class that encapsulates API access logic for Cucumber scenarios.
 * Provides high-level methods for interacting with the application API,
 * hiding low-level HTTP details from step definitions.
 */
@Component
@RequiredArgsConstructor
public class ApplicationRunner {

    private final TestRestTemplate restTemplate;
    private final RecipeJpaRepository recipeJpaRepository;
    private final CustomerJpaRepository customerJpaRepository;
    private final SubscriptionJpaRepository subscriptionJpaRepository;
    private final TestClock testClock;

    public List<Long> havingRecipes(int countOfRecipes) {
        return IntStream.rangeClosed(1, countOfRecipes)
                .mapToObj(i -> havingRecipe("Recipe " + i))
                .map(RecipeResponse::id)
                .toList();
    }

    public RecipeResponse havingRecipe(String title) {
        CreateRecipeRequest request = new CreateRecipeRequest(title);
        ResponseEntity<RecipeResponse> response = restTemplate.postForEntity(
                "/recipes",
                request,
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
        recipeJpaRepository.deleteAll();
    }

    @With
    public record SubscriptionRequest(List<Long> recipeIds, String deliveryAddress,
                                      DayOfWeek deliveryDay) {
        private SubscriptionRequest(List<Long> recipeIds) {
            this(recipeIds, anAddress(), WEDNESDAY);
        }

        public static SubscriptionRequest aSubscription(List<Long> recipeIds) {
            return new SubscriptionRequest(recipeIds);
        }
    }

    public ApiResponse<@NotNull SubscriptionResponse> signup(String token, SubscriptionRequest request) {
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

    public void processSubscriptionOrders(Long subscriptionId) {
        restTemplate.postForEntity(
                "/subscriptions/" + subscriptionId + "/process-orders",
                null,
                Void.class
        );
    }

    public ApiResponse<@NotNull SignupResponse> signupCustomer(String email, String password) {
        SignupCustomerRequest request = new SignupCustomerRequest(email, password);
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
        subscriptionJpaRepository.deleteAll();
        customerJpaRepository.deleteAll();
    }

    public void freezeTimeOn(LocalDate date) {
        testClock.freezeTime(date);
    }

    public void reset() {
        testClock.reset();
    }

    public void havingRecipes(List<String> names) {
        names.forEach(this::havingRecipe);
    }

    public record CreateRecipeRequest(String title) {
    }

    public record RecipeResponse(Long id, String title) {
    }

    public record CreateSubscriptionRequest(List<Long> recipeIds, String deliveryAddress,
                                            DayOfWeek deliveryDay) {
    }

    public record SubscriptionResponse(Long id, Long customerId, List<OrderResponse> upcomingOrders,
                                       String deliveryAddress, DayOfWeek deliveryDay) {
    }

    public record OrderResponse(Long id, List<Long> recipeIds, LocalDate deliveryDate) {
    }

    public record SignupCustomerRequest(String email, String password) {
    }

    public record SignupResponse(Long id, String email, String token) {
    }

    public record LoginRequest(String email, String password) {
    }

    public record LoginResponse(String token) {
    }
}
