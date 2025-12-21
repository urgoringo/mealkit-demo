package com.urgoringo.mealkit.subscription.application;

import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.subscription.domain.Subscriptions;
import com.urgoringo.mealkit.subscription.domain.UpcomingOrder;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@NullMarked
@Service
@RequiredArgsConstructor
public class GetSubscriptionHistoryService {

    private final Subscriptions subscriptions;

    @Transactional(readOnly = true)
    public List<UpcomingOrder> execute(Id<Customer> customerId) {
        return subscriptions.findDeliveredOrdersByCustomerId(customerId);
    }
}
