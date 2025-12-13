package com.urgoringo.mealkit.recipecatalog.domain;

import com.urgoringo.mealkit.domain.Id;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record RecipeIngredient(
        Id<Ingredient> ingredientId,
        String quantity,
        String unit
) {
    public static RecipeIngredient create(Id<Ingredient> ingredientId, String quantity, String unit) {
        return new RecipeIngredient(ingredientId, quantity, unit);
    }
}
