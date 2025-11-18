package com.urgoringo.mealkit.domain;

import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Order domain model representing a meal kit order.
 */
@NullMarked
public record Order(
        Id<Order> id,
        List<Id<Recipe>> recipeIds
) {
    /**
     * Creates a new Order without an assigned id.
     *
     * @param recipeIds the list of recipe IDs for this order
     * @return a new Order instance
     */
    public static Order create(List<Id<Recipe>> recipeIds) {
        return new Order(Id.unassigned(), recipeIds);
    }
}
