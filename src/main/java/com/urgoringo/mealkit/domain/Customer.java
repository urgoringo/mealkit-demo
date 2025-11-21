package com.urgoringo.mealkit.domain;

import org.jspecify.annotations.NullMarked;

/**
 * Customer domain model.
 */
@NullMarked
public record Customer(
        Id<Customer> id,
        String email,
        String hashedPassword
) {
    /**
     * Creates a new Customer without an assigned id.
     *
     * @param email the customer email
     * @return a new Customer instance
     */
    public static Customer create(String email) {
        return new Customer(Id.unassigned(), email, "");
    }

    /**
     * Signs up a new customer with email and password.
     *
     * @param email the customer email
     * @param hashedPassword the hashed password
     * @return a new Customer instance
     */
    public static Customer signup(String email, String hashedPassword) {
        return new Customer(Id.unassigned(), email, hashedPassword);
    }
}
