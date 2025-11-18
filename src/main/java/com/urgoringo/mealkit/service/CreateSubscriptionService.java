package com.urgoringo.mealkit.service;

import com.urgoringo.mealkit.domain.*;
import com.urgoringo.mealkit.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service for creating a new subscription.
 * Creates the customer as part of the subscription signup process.
 * Follows DDD principle: one service per use case.
 */
@NullMarked
@Service
@RequiredArgsConstructor
public class CreateSubscriptionService {

    private final CustomerDomainRepository customerDomainRepository;
    private final SubscriptionDomainRepository subscriptionDomainRepository;

    @Transactional
    public Subscription execute(String customerEmail, List<Id<Recipe>> recipeIds) {
        // Validate that customer email doesn't already exist
        if (customerDomainRepository.existsByEmail(customerEmail)) {
            throw new ValidationException("Customer with email " + customerEmail + " already exists");
        }

        // Create the customer first
        var customer = Customer.create(customerEmail);
        var savedCustomer = customerDomainRepository.save(customer);

        // Create the first order with the chosen recipes
        // Order.create() will validate minimum recipe count
        var firstOrder = Order.create(recipeIds);

        // Create subscription with the customer and first order
        var subscription = Subscription.create(savedCustomer.id(), firstOrder);

        return subscriptionDomainRepository.save(subscription);
    }
}
