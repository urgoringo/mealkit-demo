package com.urgoringo.mealkit.domain;

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
