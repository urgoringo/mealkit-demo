package com.urgoringo.mealkit.customer.application.application;

import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.customer.domain.Customers;
import com.urgoringo.mealkit.domain.ValidationFailed;
import com.urgoringo.mealkit.auth.PasswordHasher;
import com.urgoringo.mealkit.auth.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginCustomerService {

    private final Customers customers;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;

    @Transactional(readOnly = true)
    public String execute(String email, String plainPassword) {
        Customer customer = customers.findByEmail(email)
                .orElseThrow(() -> new ValidationFailed("Invalid email or password"));

        if (!passwordHasher.verify(plainPassword, customer.hashedPassword())) {
            throw new ValidationFailed("Invalid email or password");
        }

        return tokenService.generateToken(customer);
    }
}
