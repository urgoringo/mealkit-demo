package com.urgoringo.mealkit.subscription.application;

import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.subscription.domain.Subscription;
import com.urgoringo.mealkit.subscription.domain.Subscriptions;
import com.urgoringo.mealkit.domain.ValidationException;
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
        return subscriptions.findByCustomerId(customerId);
    }
}
