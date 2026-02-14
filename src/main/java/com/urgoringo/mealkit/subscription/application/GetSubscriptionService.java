package com.urgoringo.mealkit.subscription.application;

import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.subscription.domain.Subscription;
import com.urgoringo.mealkit.subscription.domain.Subscriptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetSubscriptionService {

    private final Subscriptions subscriptions;

    @Transactional
    public Subscription executeForAuthenticatedCustomer(Id<Customer> customerId) {
        return subscriptions.findByCustomerId(customerId);
    }
}
