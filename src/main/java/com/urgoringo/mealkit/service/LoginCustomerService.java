package com.urgoringo.mealkit.service;

import com.urgoringo.mealkit.domain.Customer;
import com.urgoringo.mealkit.domain.Customers;
import com.urgoringo.mealkit.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@NullMarked
@Service
@RequiredArgsConstructor
public class LoginCustomerService {

    private final Customers customers;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;

    @Transactional(readOnly = true)
    public String execute(String email, String plainPassword) {
        Customer customer = customers.findByEmail(email)
                .orElseThrow(() -> new ValidationException("Invalid email or password"));

        if (!passwordHasher.verify(plainPassword, customer.hashedPassword())) {
            throw new ValidationException("Invalid email or password");
        }

        return tokenService.generateToken(customer);
    }
}
