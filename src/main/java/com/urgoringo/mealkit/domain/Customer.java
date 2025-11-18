package com.urgoringo.mealkit.domain;

import org.jspecify.annotations.NullMarked;

/**
 * Customer domain model.
 */
@NullMarked
public record Customer(
        Id<Customer> id,
        String email
) {
    /**
     * Creates a new Customer without an assigned id.
     *
     * @param email the customer email
     * @return a new Customer instance
     */
    public static Customer create(String email) {
        return new Customer(Id.unassigned(), email);
    }
}
