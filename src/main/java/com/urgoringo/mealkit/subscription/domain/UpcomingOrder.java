package com.urgoringo.mealkit.subscription.domain;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.domain.ValidationException;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import org.jspecify.annotations.NullMarked;

import java.time.LocalDate;
import java.util.List;

@NullMarked
public record UpcomingOrder(
    Id<UpcomingOrder> id,
    List<Id<Recipe>> recipeIds,
    LocalDate deliveryDate,
    OrderStatus status
) {
    private static final int MINIMUM_RECIPE_COUNT = 3;

    public UpcomingOrder {
        if (recipeIds.size() < MINIMUM_RECIPE_COUNT) {
            throw new ValidationException("Order must contain at least " + MINIMUM_RECIPE_COUNT + " recipes");
        }
    }

    public static UpcomingOrder placed(List<Id<Recipe>> recipeIds, LocalDate deliveryDate) {
        return new UpcomingOrder(Id.unassigned(), recipeIds, deliveryDate, OrderStatus.PENDING);
    }

    public UpcomingOrder withRecipes(List<Id<Recipe>> recipeIds) {
        return new UpcomingOrder(id, recipeIds, deliveryDate, status);
    }

    public UpcomingOrder markAsDelivered() {
        return new UpcomingOrder(id, recipeIds, deliveryDate, OrderStatus.DELIVERED);
    }

    public boolean isPending() {
        return status == OrderStatus.PENDING;
    }
}
