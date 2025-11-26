package com.urgoringo.mealkit.customer.domain;

import com.urgoringo.mealkit.domain.Id;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record Customer(
        Id<Customer> id,
        String email,
        String hashedPassword
) {
    public static Customer create(String email) {
        return new Customer(Id.unassigned(), email, "");
    }

    public static Customer signup(String email, String hashedPassword) {
        return new Customer(Id.unassigned(), email, hashedPassword);
    }
}
