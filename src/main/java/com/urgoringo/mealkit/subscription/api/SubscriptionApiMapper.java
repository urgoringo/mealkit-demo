package com.urgoringo.mealkit.subscription.api;

import com.urgoringo.mealkit.subscription.api.SubscriptionController.OrderResponse;
import com.urgoringo.mealkit.subscription.api.SubscriptionController.SubscriptionResponse;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.subscription.domain.*;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionApiMapper {

    private final Clock clock;
    private final OrderPrices orderPrices;

    public SubscriptionResponse toResponse(Subscription subscription) {
        return new SubscriptionResponse(
                subscription.id().value().toString(),
                subscription.customerId().value().toString(),
                subscription.upcomingOrders().stream()
                        .map(order -> toOrderResponse(order))
                        .toList(),
                subscription.deliveryAddress(),
                subscription.deliveryDay()
        );
    }

    public OrderResponse toOrderResponse(UpcomingOrder order) {
        var totalPrice = orderPrices.totalPrice(order);
        return new OrderResponse(
                order.id().value().toString(),
                mapRecipeIdsToString(order.recipeIds()),
                order.deliveryDate(),
                order.status(),
                totalPrice.amount()
        );
    }

    public OrderResponse toOrderResponse(DeliveredOrder order) {
        var totalPrice = orderPrices.totalPrice(order);
        return new OrderResponse(
                order.id().value().toString(),
                mapRecipeIdsToString(order.recipeIds()),
                order.deliveryDate(),
                OrderStatus.DELIVERED,
                totalPrice.amount()
        );
    }

    public List<Id<Recipe>> mapRecipeIds(List<String> ids) {
        return ids.stream()
                .map(Id::<Recipe>of)
                .toList();
    }

    public List<String> mapRecipeIdsToString(List<Id<Recipe>> ids) {
        return ids.stream()
                .map(id -> id.value().toString())
                .toList();
    }
}
