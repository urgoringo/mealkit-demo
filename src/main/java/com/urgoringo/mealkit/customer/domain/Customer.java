package com.urgoringo.mealkit.customer.domain;

import com.urgoringo.mealkit.domain.Id;

public record Customer(
        Id<Customer> id,
        String email,
        String hashedPassword
) {

    public static Customer signup(String email, String hashedPassword) {
        return new Customer(Id.generate(), email, hashedPassword);
    }
}
