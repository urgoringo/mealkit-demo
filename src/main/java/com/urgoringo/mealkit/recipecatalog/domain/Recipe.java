package com.urgoringo.mealkit.recipecatalog.domain;

import com.urgoringo.mealkit.domain.Id;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record Recipe(
        Id<Recipe> id,
        String title,
        List<String> instructions,
        List<RecipeIngredient> ingredients
) {
    public static Recipe create(String title, List<String> instructions, List<RecipeIngredient> ingredients) {
        return new Recipe(
                Id.unassigned(), 
                title, 
                List.copyOf(instructions),
                List.copyOf(ingredients)
        );
    }
}
