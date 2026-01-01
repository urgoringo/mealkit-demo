package com.urgoringo.mealkit.domain;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class NotFound extends RuntimeException  {
    public NotFound(@NotNull String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return Objects.requireNonNull(super.getMessage());
    }
}
