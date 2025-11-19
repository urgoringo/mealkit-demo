package com.urgoringo.mealkit.controller;

import com.urgoringo.mealkit.domain.Subscription;
import com.urgoringo.mealkit.mapper.SubscriptionApiMapper;
import com.urgoringo.mealkit.service.CreateSubscriptionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for subscription endpoints.
 */
@NullMarked
@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final CreateSubscriptionService createSubscriptionService;
    private final SubscriptionApiMapper subscriptionApiMapper;

    @PostMapping
    public ResponseEntity<SubscriptionResponse> createSubscription(@Valid @RequestBody CreateSubscriptionRequest request) {
        var recipeIds = subscriptionApiMapper.mapRecipeIds(request.recipeIds());
        Subscription subscription = createSubscriptionService.execute(
                request.customerEmail(),
                recipeIds,
                request.deliveryAddress(),
                request.deliveryDay()
        );
        SubscriptionResponse response = subscriptionApiMapper.toResponse(subscription);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Request DTO for creating a subscription.
     */
    public record CreateSubscriptionRequest(
            @NotBlank String customerEmail,
            @NotNull List<Long> recipeIds,
            @NotBlank String deliveryAddress,
            @Nullable DayOfWeek deliveryDay
    ) {}

    /**
     * Response DTO for subscription data.
     */
    public record SubscriptionResponse(Long id, Long customerId, List<OrderResponse> upcomingOrders, String deliveryAddress, @Nullable DayOfWeek deliveryDay) {}

    /**
     * Response DTO for order data.
     */
    public record OrderResponse(Long id, List<Long> recipeIds, @Nullable LocalDate deliveryDate) {}
}
