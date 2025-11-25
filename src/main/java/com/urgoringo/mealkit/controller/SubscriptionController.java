package com.urgoringo.mealkit.controller;

import com.urgoringo.mealkit.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.domain.Subscription;
import com.urgoringo.mealkit.mapper.SubscriptionApiMapper;
import com.urgoringo.mealkit.service.CreateSubscriptionService;
import com.urgoringo.mealkit.service.GetSubscriptionService;
import com.urgoringo.mealkit.service.ProcessSubscriptionOrdersService;
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
    private final ProcessSubscriptionOrdersService processSubscriptionOrdersService;
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

    @GetMapping
    public ResponseEntity<SubscriptionResponse> getMySubscription(@AuthenticationPrincipal Id<Customer> customerId) {
        Subscription subscription = getSubscriptionService.executeForAuthenticatedCustomer(customerId);
        SubscriptionResponse response = subscriptionApiMapper.toResponse(subscription);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/process-orders")
    public ResponseEntity<Void> processSubscriptionOrders(@PathVariable Long id) {
        processSubscriptionOrdersService.execute(Id.of(id));
        return ResponseEntity.ok().build();
    }

    public record CreateSubscriptionRequest(
            @NotBlank String customerEmail,
            @NotNull List<Long> recipeIds,
            @NotBlank String deliveryAddress,
            @Nullable DayOfWeek deliveryDay
    ) {}

    public record SubscriptionResponse(Long id, Long customerId, List<OrderResponse> upcomingOrders, String deliveryAddress, @Nullable DayOfWeek deliveryDay) {}

    public record OrderResponse(Long id, List<Long> recipeIds, @Nullable LocalDate deliveryDate) {}
}
