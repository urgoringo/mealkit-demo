package com.urgoringo.mealkit.domain;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;

/**
 * Wrapper type for entity identifiers.
 * Provides type-safe IDs and handles the case of entities without assigned IDs.
 *
 * @param <T> the entity type this ID belongs to
 */
@NullMarked
public record Id<T>(Long value) {

    /**
     * Creates an unassigned ID for a new entity.
     * Uses NullUnmarked to opt out of null safety checks for this factory method.
     *
     * @param <T> the entity type
     * @return an ID with null value
     */
    @NullUnmarked
    public static <T> Id<T> unassigned() {
        return new Id<>(null);
    }

    /**
     * Creates an ID from a non-null Long value.
     *
     * @param value the ID value
     * @param <T> the entity type
     * @return an ID with the given value
     */
    public static <T> Id<T> of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("Use unassigned() for IDs without a value");
        }
        return new Id<>(value);
    }

    /**
     * Checks if this ID has been assigned a value.
     *
     * @return true if the ID has a value
     */
    public boolean isAssigned() {
        return value != null;
    }
}
