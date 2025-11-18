package com.urgoringo.mealkit.domain;

import org.jspecify.annotations.NullMarked;

/**
 * Recipe domain model representing a meal kit recipe.
 */
@NullMarked
public record Recipe(
        Id<Recipe> id,
        String title
) {
    /**
     * Creates a new Recipe without an assigned id.
     *
     * @param title the recipe title
     * @return a new Recipe instance
     */
    public static Recipe create(String title) {
        return new Recipe(Id.unassigned(), title);
    }
}
