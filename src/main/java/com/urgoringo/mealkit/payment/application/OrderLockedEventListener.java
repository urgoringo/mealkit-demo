package com.urgoringo.mealkit.payment.application;

import com.urgoringo.mealkit.payment.domain.PaymentGateway;
import com.urgoringo.mealkit.subscription.domain.OrderLockedEvent;
import com.urgoringo.mealkit.subscription.domain.OrderPrices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderLockedEventListener {

    private final PaymentGateway paymentGateway;
    private final OrderPrices orderPrices;

    @EventListener
    public void onOrderLocked(OrderLockedEvent event) {
        log.info("Handling OrderLockedEvent for customer {} and order {}", 
            event.customerId(), event.lockedOrder().id());
        var totalPrice = orderPrices.totalPrice(event.lockedOrder());
        paymentGateway.chargeCustomer(event.customerId(), totalPrice);
    }
}
