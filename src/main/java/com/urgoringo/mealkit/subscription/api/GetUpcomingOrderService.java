package com.urgoringo.mealkit.subscription.api;

import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.subscription.domain.Order;
import com.urgoringo.mealkit.subscription.domain.Subscriptions;
import com.urgoringo.mealkit.subscription.domain.UpcomingOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUpcomingOrderService {

    private final Subscriptions subscriptions;

    public UpcomingOrder execute(Id<Customer> customerId, Id<Order> orderId) {
        return subscriptions.findUpcomingOrderBy(customerId, orderId);
    }
}
