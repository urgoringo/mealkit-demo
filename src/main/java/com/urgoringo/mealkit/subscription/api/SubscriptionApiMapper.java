package com.urgoringo.mealkit.subscription.api;

import com.urgoringo.mealkit.subscription.api.SubscriptionController.OrderResponse;
import com.urgoringo.mealkit.subscription.api.SubscriptionController.SubscriptionResponse;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.subscription.domain.*;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.List;

@NullMarked
@Component
@RequiredArgsConstructor
public class SubscriptionApiMapper {

    private final Clock clock;

    public SubscriptionResponse toResponse(Subscription subscription) {
        return new SubscriptionResponse(
                subscription.id().value(),
                subscription.customerId().value(),
                subscription.upcomingOrders().stream()
                        .map(order -> toOrderResponse(order))
                        .toList(),
                subscription.deliveryAddress(),
                subscription.deliveryDay()
        );
    }

    public OrderResponse toOrderResponse(UpcomingOrder order) {
        return new OrderResponse(
                order.id().value(),
                mapRecipeIdsToLong(order.recipeIds()),
                order.deliveryDate(),
                order.status()
        );
    }

    public OrderResponse toOrderResponse(DeliveredOrder order) {
        return new OrderResponse(
                order.id().value(),
                mapRecipeIdsToLong(order.recipeIds()),
                order.deliveryDate(),
                OrderStatus.DELIVERED
        );
    }

    public List<Id<Recipe>> mapRecipeIds(List<Long> ids) {
        return ids.stream()
                .map(Id::<Recipe>of)
                .toList();
    }

    public List<Long> mapRecipeIdsToLong(List<Id<Recipe>> ids) {
        return ids.stream()
                .map(Id::value)
                .toList();
    }
}
