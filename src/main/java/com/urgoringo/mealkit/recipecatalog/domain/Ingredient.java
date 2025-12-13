package com.urgoringo.mealkit.recipecatalog.domain;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record Ingredient(
        String name,
        String quantity,
        String unit
) {
    public static Ingredient create(String name, String quantity, String unit) {
        return new Ingredient(name, quantity, unit);
    }
}
