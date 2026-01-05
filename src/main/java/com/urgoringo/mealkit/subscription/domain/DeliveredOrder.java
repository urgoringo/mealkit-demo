package com.urgoringo.mealkit.subscription.domain;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;

import java.time.LocalDate;
import java.util.List;

import static com.urgoringo.mealkit.subscription.domain.OrderStatus.DELIVERED;

public record DeliveredOrder(
    Id<Order> id,
    List<Id<Recipe>> recipeIds,
    LocalDate deliveryDate
) implements Order {
    @Override
    public OrderStatus status() {
        return DELIVERED;
    }
}
