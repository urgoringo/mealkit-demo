package com.urgoringo.mealkit.subscription.domain;

import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.domain.Id;

public record OrderLockedEvent(
    Id<Customer> customerId,
    LockedOrder lockedOrder
) {
}
