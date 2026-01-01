package com.urgoringo.mealkit.customer.application.application;

import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.customer.domain.Customers;
import com.urgoringo.mealkit.domain.ValidationFailed;
import com.urgoringo.mealkit.auth.PasswordHasher;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for customer signup.
 * Follows DDD principle: one service per use case.
 */
@NullMarked
@Service
@RequiredArgsConstructor
public class SignupCustomerService {

    private final Customers customers;
    private final PasswordHasher passwordHasher;

    @Transactional
    public Customer execute(String email, String plainPassword) {
        if (customers.existsByEmail(email)) {
            throw new ValidationFailed("Customer with email " + email + " already exists");
        }

        String hashedPassword = passwordHasher.hash(plainPassword);
        Customer customer = Customer.signup(email, hashedPassword);

        return customers.add(customer);
    }
}
