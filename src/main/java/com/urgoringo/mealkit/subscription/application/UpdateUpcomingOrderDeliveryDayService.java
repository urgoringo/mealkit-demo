package com.urgoringo.mealkit.subscription.application;

import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.subscription.domain.Subscription;
import com.urgoringo.mealkit.subscription.domain.Subscriptions;
import com.urgoringo.mealkit.subscription.domain.UpcomingOrder;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.DayOfWeek;

@NullMarked
@Service
@RequiredArgsConstructor
public class UpdateUpcomingOrderDeliveryDayService {

    private final Subscriptions subscriptions;
    private final Clock clock;

    @Transactional
    public Subscription execute(Id<Customer> customerId, Id<UpcomingOrder> orderId, DayOfWeek deliveryDay) {
        Subscription subscription = subscriptions.findByCustomerId(customerId);
        Subscription updatedSubscription = subscription.withUpdatedOrderDeliveryDate(orderId, deliveryDay, clock);
        return subscriptions.save(updatedSubscription);
    }
}
