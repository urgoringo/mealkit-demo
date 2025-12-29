package com.urgoringo.mealkit.recipecatalog.domain;

import com.urgoringo.mealkit.domain.Id;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record Recipe(
        Id<Recipe> id,
        String title,
        List<String> instructions,
        List<RecipeIngredient> ingredients,
        Id<RecipePricingCategory> pricingCategoryId
) {
    public Recipe {
        if (ingredients.isEmpty()) {
            throw new IllegalArgumentException("Recipe must have at least one ingredient");
        }
    }

    public static Recipe create(String title, List<String> instructions, List<RecipeIngredient> ingredients, Id<RecipePricingCategory> pricingCategoryId) {
        return new Recipe(
                Id.unassigned(), 
                title, 
                List.copyOf(instructions),
                List.copyOf(ingredients),
                pricingCategoryId
        );
    }
}
