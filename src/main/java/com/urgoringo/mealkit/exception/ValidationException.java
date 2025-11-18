package com.urgoringo.mealkit.exception;

import org.jspecify.annotations.NullMarked;

import java.util.Objects;

/**
 * Exception thrown when validation fails.
 * Results in HTTP 422 Unprocessable Entity response.
 * Always includes a non-null error message.
 */
@NullMarked
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(Objects.requireNonNull(message, "Validation exception message must not be null"));
    }

    @Override
    public String getMessage() {
        return Objects.requireNonNull(super.getMessage());
    }
}
