package com.urgoringo.mealkit.recipecatalog.domain;

import com.urgoringo.mealkit.domain.ValidationException;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

@NullMarked
public record Quantity(String amount, String unit) {

    private static final Set<String> VALID_UNITS = Set.of("g", "piece", "cup");

    public static Quantity of(String amount, String unit) {
        if (!VALID_UNITS.contains(unit)) {
            throw new ValidationException("Invalid unit: " + unit + ". Supported units are: " + String.join(", ", VALID_UNITS));
        }
        return new Quantity(amount, unit);
    }
}
