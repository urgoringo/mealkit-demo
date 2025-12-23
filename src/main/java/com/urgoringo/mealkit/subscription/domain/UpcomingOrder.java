package com.urgoringo.mealkit.subscription.domain;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.domain.ValidationException;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import org.jspecify.annotations.NullMarked;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@NullMarked
public record UpcomingOrder(
    Id<UpcomingOrder> id,
    List<Id<Recipe>> recipeIds,
    LocalDate deliveryDate
) {
    private static final int MINIMUM_RECIPE_COUNT = 3;
    private static final int DAYS_BEFORE_DELIVERY_TO_LOCK = 3;

    public UpcomingOrder {
        if (recipeIds.size() < MINIMUM_RECIPE_COUNT) {
            throw new ValidationException("Order must contain at least " + MINIMUM_RECIPE_COUNT + " recipes");
        }
    }

    public static UpcomingOrder placed(List<Id<Recipe>> recipeIds, LocalDate deliveryDate) {
        return new UpcomingOrder(Id.unassigned(), recipeIds, deliveryDate);
    }

    public UpcomingOrder withUpdatedRecipes(List<Id<Recipe>> recipeIds) {
//        if (status == OrderStatus.LOCKED) {
//            throw new ValidationException("Cannot update locked order");
//        }
        return new UpcomingOrder(id, recipeIds, deliveryDate);
    }

    public DeliveredOrder markAsDelivered(Clock clock) {
        if (!id.isAssigned()) {
            throw new IllegalStateException("Cannot mark unassigned order as delivered");
        }
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

    public OrderStatus status(Clock clock) {
        LocalDate currentDate = LocalDate.now(clock);
        long daysUntilDelivery = currentDate.until(deliveryDate, DAYS);
        
        if (daysUntilDelivery <= DAYS_BEFORE_DELIVERY_TO_LOCK) {
            return OrderStatus.LOCKED;
        }
        
        return OrderStatus.PENDING;
    }

}
