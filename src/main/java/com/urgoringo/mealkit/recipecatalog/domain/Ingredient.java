package com.urgoringo.mealkit.recipecatalog.domain;

import com.urgoringo.mealkit.domain.Id;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record Ingredient(
        Id<Ingredient> id,
        String name
) {
    public static Ingredient create(String name) {
        return new Ingredient(Id.generate(), name);
    }
}
