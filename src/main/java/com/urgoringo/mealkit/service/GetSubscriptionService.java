package com.urgoringo.mealkit.service;

import com.urgoringo.mealkit.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.domain.Subscription;
import com.urgoringo.mealkit.domain.SubscriptionDomainRepository;
import com.urgoringo.mealkit.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for retrieving authenticated customer's subscription.
 * Follows DDD principle: one service per use case.
 */
@NullMarked
@Service
@RequiredArgsConstructor
public class GetSubscriptionService {

    private final SubscriptionDomainRepository subscriptionDomainRepository;

    @Transactional(readOnly = true)
    public Subscription executeForAuthenticatedCustomer(Id<Customer> customerId) {
        return subscriptionDomainRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ValidationException("Subscription not found"));
    }
}
