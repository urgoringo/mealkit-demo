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
    private final Clock clock;

    @Transactional
    public Subscription execute(String customerEmail, List<Id<Recipe>> recipeIds, String deliveryAddress, @Nullable DayOfWeek deliveryDay) {
        if (customerDomainRepository.existsByEmail(customerEmail)) {
            throw new ValidationException("Customer with email " + customerEmail + " already exists");
        }

        var customer = Customer.create(customerEmail);
        var savedCustomer = customerDomainRepository.save(customer);

        LocalDate today = LocalDate.now(clock);
        var subscription = Subscription.signup(savedCustomer.id(), recipeIds, deliveryAddress, deliveryDay, today);

        return subscriptionDomainRepository.save(subscription);
    }
}
