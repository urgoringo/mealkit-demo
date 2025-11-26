package com.urgoringo.mealkit.recipecatalog.domain;

import com.urgoringo.mealkit.domain.Id;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record Recipe(
        Id<Recipe> id,
        String title
) {
    public static Recipe create(String title) {
        return new Recipe(Id.unassigned(), title);
    }
}
