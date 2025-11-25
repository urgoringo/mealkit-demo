package com.urgoringo.mealkit.service;

import com.urgoringo.mealkit.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.domain.Subscription;
import com.urgoringo.mealkit.domain.Subscriptions;
import com.urgoringo.mealkit.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@NullMarked
@Service
@RequiredArgsConstructor
public class GetSubscriptionService {

    private final Subscriptions subscriptions;

    @Transactional(readOnly = true)
    public Subscription executeForAuthenticatedCustomer(Id<Customer> customerId) {
        return subscriptions.findByCustomerId(customerId)
                .orElseThrow(() -> new ValidationException("Subscription not found"));
    }
}
