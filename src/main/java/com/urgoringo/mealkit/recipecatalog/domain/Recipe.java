package com.urgoringo.mealkit.recipecatalog.domain;

import com.urgoringo.mealkit.domain.Id;

import java.util.List;

public record Recipe(
        Id<Recipe> id,
        String title,
        List<String> instructions,
        List<RecipeIngredient> ingredients,
        PricingCategory pricingCategory
) {
    public Recipe {
        if (ingredients.isEmpty()) {
            throw new IllegalArgumentException("Recipe must have at least one ingredient");
        }
    }

    public static Recipe create(String title, List<String> instructions, List<RecipeIngredient> ingredients, PricingCategory pricingCategory) {
        return new Recipe(
                Id.generate(), 
                title, 
                List.copyOf(instructions),
                List.copyOf(ingredients),
                pricingCategory
        );
    }
}
