package com.urgoringo.mealkit.infra;

import com.urgoringo.mealkit.domain.NotFound;
import com.urgoringo.mealkit.domain.ValidationFailed;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.*;

/**
 * Global exception handler for REST controllers.
 */
@NullMarked
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationFailed.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationFailed ex) {
        String message = ex.getMessage();
        ErrorResponse error = new ErrorResponse(message);
        return ResponseEntity.status(UNPROCESSABLE_CONTENT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Validation error");
        ErrorResponse error = new ErrorResponse(message);
        return ResponseEntity.status(UNPROCESSABLE_CONTENT).body(error);
    }

    @ExceptionHandler(NotFound.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(HttpRequest request, NotFound ex) {
        String message = ex.getMessage();
        ErrorResponse error = new ErrorResponse(message);

        if (isQuery(request)) {
            return ResponseEntity.status(NO_CONTENT).build();
        }
        return ResponseEntity.status(UNPROCESSABLE_CONTENT).body(error);
    }

    private static boolean isQuery(HttpRequest request) {
        return request.getMethod() == GET || request.getMethod() == HEAD;
    }

    /**
     * Error response DTO.
     */
    public record ErrorResponse(String message) {}
}
