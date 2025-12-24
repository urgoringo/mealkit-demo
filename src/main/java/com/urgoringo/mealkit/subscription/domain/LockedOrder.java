package com.urgoringo.mealkit.subscription.domain;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.domain.ValidationException;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public record LockedOrder(
    Id<Order> id,
    List<Id<Recipe>> recipeIds,
    LocalDate deliveryDate
) implements UpcomingOrder {

    public DeliveredOrder markAsDelivered(Clock clock) {
        LocalDate currentDate = LocalDate.now(clock);
        if (currentDate.isBefore(deliveryDate)) {
            throw new ValidationException("Cannot deliver order before delivery date");
        }
        return new DeliveredOrder(
            Id.of(id.value()),
            recipeIds,
            deliveryDate
        );
    }

    @Override
    public OrderStatus status() {
        return OrderStatus.LOCKED;
    }
}
