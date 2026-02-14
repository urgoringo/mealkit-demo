package com.urgoringo.mealkit.payment.domain;

import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.domain.Money;

public record CustomerChargedEvent(Id<Customer> customerId, Money amount) {
}
