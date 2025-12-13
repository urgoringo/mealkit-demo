package com.urgoringo.mealkit.recipecatalog.domain;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum Unit {
    G("g"),
    PIECE("piece"),
    CUP("cup");

    private final String displayName;

    Unit(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Unit fromString(String value) {
        for (Unit unit : values()) {
            if (unit.displayName.equals(value)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Unknown unit: " + value);
    }
}
