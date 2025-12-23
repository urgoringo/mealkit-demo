package com.urgoringo.mealkit.subscription.api;

import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.subscription.domain.OrderStatus;
import com.urgoringo.mealkit.subscription.domain.Subscription;
import com.urgoringo.mealkit.subscription.application.CreateSubscriptionService;
import com.urgoringo.mealkit.subscription.application.GetSubscriptionHistoryService;
import com.urgoringo.mealkit.subscription.application.GetSubscriptionService;
import com.urgoringo.mealkit.subscription.application.UpdateUpcomingOrderRecipesService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@NullMarked
@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final CreateSubscriptionService createSubscriptionService;
    private final GetSubscriptionService getSubscriptionService;
    private final GetSubscriptionHistoryService getSubscriptionHistoryService;
    private final UpdateUpcomingOrderRecipesService updateUpcomingOrderRecipesService;
    private final SubscriptionApiMapper subscriptionApiMapper;

    @PostMapping
    public ResponseEntity<SubscriptionResponse> createSubscription(
            @AuthenticationPrincipal Id<Customer> customerId,
            @Valid @RequestBody CreateSubscriptionRequest request) {
        var recipeIds = subscriptionApiMapper.mapRecipeIds(request.recipeIds());
        Subscription subscription = createSubscriptionService.execute(
                customerId,
                recipeIds,
                request.deliveryAddress(),
                request.deliveryDay()
        );
        SubscriptionResponse response = subscriptionApiMapper.toResponse(subscription);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<SubscriptionResponse> getMySubscription(@AuthenticationPrincipal Id<Customer> customerId) {
        Subscription subscription = getSubscriptionService.executeForAuthenticatedCustomer(customerId);
        SubscriptionResponse response = subscriptionApiMapper.toResponse(subscription);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/upcoming-order/recipes")
    public ResponseEntity<SubscriptionResponse> updateUpcomingOrderRecipes(
            @AuthenticationPrincipal Id<Customer> customerId,
            @Valid @RequestBody UpdateUpcomingOrderRecipesRequest request) {
        var recipeIds = subscriptionApiMapper.mapRecipeIds(request.recipeIds());
        Subscription subscription = updateUpcomingOrderRecipesService.execute(customerId, recipeIds);
        SubscriptionResponse response = subscriptionApiMapper.toResponse(subscription);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<OrderResponse>> getSubscriptionHistory(@AuthenticationPrincipal Id<Customer> customerId) {
        var deliveredOrders = getSubscriptionHistoryService.execute(customerId);
        List<OrderResponse> response = deliveredOrders.stream()
                .map(subscriptionApiMapper::toOrderResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    public record CreateSubscriptionRequest(
            @NotNull List<Long> recipeIds,
            @NotBlank String deliveryAddress,
            @NotNull DayOfWeek deliveryDay
    ) {}

    public record UpdateUpcomingOrderRecipesRequest(
            @NotNull List<Long> recipeIds
    ) {}

    public record SubscriptionResponse(Long id, Long customerId, List<OrderResponse> upcomingOrders, String deliveryAddress, DayOfWeek deliveryDay) {}

    public record OrderResponse(Long id, List<Long> recipeIds, LocalDate deliveryDate, OrderStatus status) {}
}
