package com.urgoringo.mealkit.service;

import com.urgoringo.mealkit.domain.Customer;
import com.urgoringo.mealkit.domain.CustomerDomainRepository;
import com.urgoringo.mealkit.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for customer login.
 * Follows DDD principle: one service per use case.
 */
@NullMarked
@Service
@RequiredArgsConstructor
public class LoginCustomerService {

    private final CustomerDomainRepository customerDomainRepository;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;

    @Transactional(readOnly = true)
    public String execute(String email, String plainPassword) {
        Customer customer = customerDomainRepository.findByEmail(email)
                .orElseThrow(() -> new ValidationException("Invalid email or password"));

        if (!passwordHasher.verify(plainPassword, customer.hashedPassword())) {
            throw new ValidationException("Invalid email or password");
        }

        return tokenService.generateToken(customer);
    }
}
