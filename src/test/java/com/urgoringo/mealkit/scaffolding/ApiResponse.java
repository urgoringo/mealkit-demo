package com.urgoringo.mealkit.scaffolding;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;

@NullMarked
public sealed interface ApiResponse<T> {

    boolean isSuccess();

    record Success<T>(T value) implements ApiResponse<T> {
        @Override
        public boolean isSuccess() {
            return true;
        }
    }

    record Error<T>(int statusCode, String body) implements ApiResponse<T> {
        @Override
        public boolean isSuccess() {
            return false;
        }
    }

    default T expectSuccess() {
        return switch (this) {
            case Success<T> success -> success.value();
            case Error<T> error -> throw new AssertionError("Expected success but got error. Status: " +
                error.statusCode() + ", Body: " + error.body());
        };
    }

    default int expectError() {
        return switch (this) {
            case Success<T> _ -> throw new AssertionError("Expected error but got success");
            case Error<T> error -> error.statusCode();
        };
    }

    static <T> ApiResponse<T> from(ResponseEntity<T> response) {
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return new Success<>(response.getBody());
        } else {
            String errorBody = response.getBody() != null ? response.getBody().toString() : "";
            return new Error<>(response.getStatusCode().value(), errorBody);
        }
    }
}
