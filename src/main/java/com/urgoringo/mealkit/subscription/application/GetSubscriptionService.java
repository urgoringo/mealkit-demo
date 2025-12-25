package com.urgoringo.mealkit.subscription.application;

import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.subscription.domain.Subscription;
import com.urgoringo.mealkit.subscription.domain.Subscriptions;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@NullMarked
@Service
@RequiredArgsConstructor
public class GetSubscriptionService {

    private final Subscriptions subscriptions;
    private final Clock clock;

    @Transactional
    public Subscription executeForAuthenticatedCustomer(Id<Customer> customerId) {
        Subscription subscription = subscriptions.findByCustomerId(customerId);
        Subscription updatedSubscription = subscription.withLockedUpcomingOrder(clock);
        
        if (updatedSubscription != subscription) {
            return subscriptions.save(updatedSubscription);
        }
        
        return subscription;
    }
}
