package com.urgoringo.mealkit.subscription.domain;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.domain.ValidationException;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
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

    public Order {
        if (recipeIds.size() < MINIMUM_RECIPE_COUNT) {
            throw new ValidationException("Order must contain at least " + MINIMUM_RECIPE_COUNT + " recipes");
        }
    }

    public static Order placed(List<Id<Recipe>> recipeIds, @Nullable LocalDate deliveryDate) {
        return new Order(Id.unassigned(), recipeIds, deliveryDate);
    }

    public Order withRecipes(List<Id<Recipe>> recipeIds) {
        return new Order(id, recipeIds, deliveryDate);
    }
}
