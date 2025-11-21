package com.urgoringo.mealkit.service;

import com.urgoringo.mealkit.domain.*;
import com.urgoringo.mealkit.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Application service for creating a new subscription.
 * Creates the customer as part of the subscription signup process with a temporary password.
 * Follows DDD principle: one service per use case.
 */
@NullMarked
@Service
@RequiredArgsConstructor
public class CreateSubscriptionService {

    private final CustomerDomainRepository customerDomainRepository;
    private final SubscriptionDomainRepository subscriptionDomainRepository;
    private final PasswordHasher passwordHasher;
    private final Clock clock;

    @Transactional
    public Subscription execute(String customerEmail, List<Id<Recipe>> recipeIds, String deliveryAddress, @Nullable DayOfWeek deliveryDay) {
        if (customerDomainRepository.existsByEmail(customerEmail)) {
            throw new ValidationException("Customer with email " + customerEmail + " already exists");
        }

        // Create customer with a temporary password
        // In production, this would trigger a password reset email
        String temporaryPassword = "TEMP_" + UUID.randomUUID();
        String hashedPassword = passwordHasher.hash(temporaryPassword);
        var customer = Customer.signup(customerEmail, hashedPassword);
        var savedCustomer = customerDomainRepository.save(customer);

        LocalDate today = LocalDate.now(clock);
        var subscription = Subscription.signup(savedCustomer.id(), recipeIds, deliveryAddress, deliveryDay, today);

        return subscriptionDomainRepository.save(subscription);
    }
}
