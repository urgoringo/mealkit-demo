package com.urgoringo.mealkit.scaffolding;

import com.urgoringo.mealkit.payment.domain.CustomerChargedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerChargedEventListener {

    private final BillingSystemDouble billingSystemDouble;

    @EventListener
    public void onCustomerCharged(CustomerChargedEvent event) {
        billingSystemDouble.recordCharge(event.amount().amount());
    }
}
