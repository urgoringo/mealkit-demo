package com.urgoringo.mealkit.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

/**
 * Wrapper type for entity identifiers using UUIDv7.
 * Provides type-safe IDs with time-ordered UUIDs for better database performance.
 *
 * @param <T> the entity type this ID belongs to
 */
@NullMarked
public record Id<T>(UUID value) {

    /**
     * Creates a new ID with a generated UUIDv7.
     * UUIDv7 provides time-ordered IDs that are database-friendly.
     *
     * @param <T> the entity type
     * @return an ID with a new UUIDv7 value
     */
    public static <T> Id<T> generate() {
        return new Id<>(UuidCreator.getTimeOrderedEpoch());
    }

    /**
     * Creates an ID from a non-null UUID value.
     *
     * @param value the UUID value
     * @param <T> the entity type
     * @return an ID with the given value
     */
    public static <T> Id<T> of(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UUID value cannot be null");
        }
        return new Id<>(value);
    }

    /**
     * Creates an ID from a string representation of a UUID.
     *
     * @param value the UUID string
     * @param <T> the entity type
     * @return an ID with the parsed UUID value
     */
    public static <T> Id<T> of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("UUID string cannot be null");
        }
        return new Id<>(UUID.fromString(value));
    }
}
