package com.urgoringo.mealkit.domain;

import com.urgoringo.mealkit.exception.ValidationException;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.util.List;

@NullMarked
public record Order(
        Id<Order> id,
        List<Id<Recipe>> recipeIds,
        @Nullable LocalDate deliveryDate
) {
    private static final int MINIMUM_RECIPE_COUNT = 3;

    public static Order placed(List<Id<Recipe>> recipeIds, @Nullable LocalDate deliveryDate) {
        if (recipeIds.size() < MINIMUM_RECIPE_COUNT) {
            throw new ValidationException("Order must contain at least " + MINIMUM_RECIPE_COUNT + " recipes");
        }
        return new Order(Id.unassigned(), recipeIds, deliveryDate);
    }
}
