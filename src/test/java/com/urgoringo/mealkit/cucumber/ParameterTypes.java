package com.urgoringo.mealkit.cucumber;

import io.cucumber.java.ParameterType;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Custom Cucumber parameter types for more descriptive step definitions.
 * These allow using meaningful names like {email} instead of generic {word}.
 */
public class ParameterTypes {

    /**
     * Matches email addresses in step definitions.
     * Example: "customer with email: {email} already exists"
     */
    @ParameterType("\\S+@\\S+\\.\\S+")
    public String email(String value) {
        return value;
    }

    /**
     * Matches day of week names (Monday, Tuesday, etc.).
     * Example: "customer selects {dayOfWeek} as the delivery day"
     */
    @ParameterType("Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday")
    public DayOfWeek dayOfWeek(String value) {
        return DayOfWeek.valueOf(value.toUpperCase());
    }

    /**
     * Matches dates in yyyy.MM.dd format.
     * Example: "today is {date}" or "first order will be delivered on {date}"
     */
    @ParameterType("\\d{4}\\.\\d{2}\\.\\d{2}")
    public LocalDate date(String value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return LocalDate.parse(value, formatter);
    }

    /**
     * Matches HTTP status codes.
     * Example: "system returns {statusCode} with validation error"
     */
    @ParameterType("\\d{3}")
    public Integer statusCode(String value) {
        return Integer.parseInt(value);
    }

    /**
     * Matches recipe counts.
     * Example: "{recipeCount} recipes are available in the system"
     */
    @ParameterType("\\d+")
    public Integer recipeCount(String value) {
        return Integer.parseInt(value);
    }
}
