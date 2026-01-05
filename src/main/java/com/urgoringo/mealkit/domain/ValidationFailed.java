package com.urgoringo.mealkit.domain;
import java.util.Objects;

/**
 * Exception thrown when validation fails.
 * Results in HTTP 422 Unprocessable Entity response.
 * Always includes a non-null error message.
 */
public class ValidationFailed extends RuntimeException {

    public ValidationFailed(String message) {
        super(Objects.requireNonNull(message, "Validation exception message must not be null"));
    }

    @Override
    public String getMessage() {
        return Objects.requireNonNull(super.getMessage());
    }
}
