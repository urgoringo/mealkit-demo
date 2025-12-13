package com.urgoringo.mealkit.recipecatalog.domain;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record Quantity(String amount, Unit unit) {

    public static Quantity of(String amount, Unit unit) {
        return new Quantity(amount, unit);
    }
}
