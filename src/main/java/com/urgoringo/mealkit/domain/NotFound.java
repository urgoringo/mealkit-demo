package com.urgoringo.mealkit.domain;

import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class NotFound extends RuntimeException  {
    public NotFound(@NotNull String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return Objects.requireNonNull(super.getMessage());
    }
}
