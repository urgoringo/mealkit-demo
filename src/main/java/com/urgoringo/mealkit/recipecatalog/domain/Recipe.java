package com.urgoringo.mealkit.recipecatalog.domain;

import com.urgoringo.mealkit.domain.Id;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record Recipe(
        Id<Recipe> id,
        String title,
        List<String> ingredients,
        List<String> instructions
) {
    public static Recipe create(String title, List<String> ingredients, List<String> instructions) {
        return new Recipe(Id.unassigned(), title, List.copyOf(ingredients), List.copyOf(instructions));
    }
}
