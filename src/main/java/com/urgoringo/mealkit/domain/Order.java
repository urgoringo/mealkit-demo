package com.urgoringo.mealkit.domain;

import com.urgoringo.mealkit.exception.ValidationException;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * Order domain model representing a meal kit order.
 * Enforces business rule: must contain at least 3 recipes.
 */
@NullMarked
public record Order(
        Id<Order> id,
        List<Id<Recipe>> recipeIds,
        @Nullable LocalDate deliveryDate
) {
    private static final int MINIMUM_RECIPE_COUNT = 3;

    /**
     * Creates a new Order without an assigned id.
     *
     * @param recipeIds the list of recipe IDs for this order
     * @param deliveryDate the delivery date (optional)
     * @return a new Order instance
     * @throws ValidationException if fewer than 3 recipes are provided
     */
    public static Order create(List<Id<Recipe>> recipeIds, @Nullable LocalDate deliveryDate) {
        if (recipeIds.size() < MINIMUM_RECIPE_COUNT) {
            throw new ValidationException("Order must contain at least " + MINIMUM_RECIPE_COUNT + " recipes");
        }
        return new Order(Id.unassigned(), recipeIds, deliveryDate);
    }
}
