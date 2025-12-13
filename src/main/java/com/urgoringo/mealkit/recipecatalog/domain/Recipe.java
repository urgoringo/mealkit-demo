package com.urgoringo.mealkit.recipecatalog.domain;

import com.urgoringo.mealkit.domain.Id;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record Recipe(
        Id<Recipe> id,
        String title,
        List<String> instructions,
        List<Ingredient> ingredientsWithDetails
) {
    public static Recipe create(String title, List<String> instructions, List<Ingredient> ingredientsWithDetails) {
        return new Recipe(
                Id.unassigned(), 
                title, 
                List.copyOf(instructions),
                List.copyOf(ingredientsWithDetails)
        );
    }
}
