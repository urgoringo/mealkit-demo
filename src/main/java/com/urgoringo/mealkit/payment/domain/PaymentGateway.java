package com.urgoringo.mealkit.payment.domain;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.domain.Money;
import com.urgoringo.mealkit.customer.domain.Customer;

public interface PaymentGateway {
    
    void chargeCustomer(Id<Customer> customerId, Money amount);
}
