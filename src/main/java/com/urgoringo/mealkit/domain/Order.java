package com.urgoringo.mealkit.domain;

import com.urgoringo.mealkit.exception.ValidationException;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Order domain model representing a meal kit order.
 * Enforces business rule: must contain at least 3 recipes.
 */
@NullMarked
public record Order(
        Id<Order> id,
        List<Id<Recipe>> recipeIds
) {
    private static final int MINIMUM_RECIPE_COUNT = 3;

    /**
     * Creates a new Order without an assigned id.
     *
     * @param recipeIds the list of recipe IDs for this order
     * @return a new Order instance
     * @throws ValidationException if fewer than 3 recipes are provided
     */
    public static Order create(List<Id<Recipe>> recipeIds) {
        if (recipeIds.size() < MINIMUM_RECIPE_COUNT) {
            throw new ValidationException("Order must contain at least " + MINIMUM_RECIPE_COUNT + " recipes");
        }
        return new Order(Id.unassigned(), recipeIds);
    }
}
