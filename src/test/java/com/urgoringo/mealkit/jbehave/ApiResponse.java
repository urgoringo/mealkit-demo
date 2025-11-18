package com.urgoringo.mealkit.jbehave;

import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;

/**
 * Sealed interface representing an API response that can be either successful or an error.
 *
 * @param <T> the type of the successful response body
 */
@NullMarked
public sealed interface ApiResponse<T> {

    /**
     * Successful API response containing the response body.
     *
     * @param value the successful response body
     * @param <T> the type of the response body
     */
    record Success<T>(T value) implements ApiResponse<T> {}

    /**
     * Error API response containing the status code and error message.
     *
     * @param statusCode the HTTP status code
     * @param body the error response body
     * @param <T> the expected type of successful response (not used in error case)
     */
    record Error<T>(int statusCode, String body) implements ApiResponse<T> {}

    /**
     * Factory method to create ApiResponse from a ResponseEntity.
     *
     * @param response the response entity
     * @param <T> the type of the successful response body
     * @return Success if status is 2xx and body is non-null, Error otherwise
     */
    static <T> ApiResponse<T> from(ResponseEntity<T> response) {
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return new Success<>(response.getBody());
        } else {
            String errorBody = response.getBody() != null ? response.getBody().toString() : "";
            return new Error<>(response.getStatusCode().value(), errorBody);
        }
    }
}
