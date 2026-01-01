package com.urgoringo.mealkit.recipecatalog.domain;

import com.urgoringo.mealkit.domain.Id;

public record RecipeIngredient(
        Id<Ingredient> ingredientId,
        Quantity quantity
) {
    public static RecipeIngredient create(Id<Ingredient> ingredientId, Quantity quantity) {
        return new RecipeIngredient(ingredientId, quantity);
    }
}
