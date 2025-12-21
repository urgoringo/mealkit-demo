package com.urgoringo.mealkit.subscription.application;

import com.urgoringo.mealkit.backoffice.domain.BackofficeUser;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.subscription.domain.DeliveredOrder;
import com.urgoringo.mealkit.subscription.domain.Subscriptions;
import com.urgoringo.mealkit.subscription.domain.UpcomingOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@NullMarked
@Service
@RequiredArgsConstructor
public class MarkOrderDeliveredService {

    private final Subscriptions subscriptions;

    @Transactional
    public void execute(Id<BackofficeUser> backofficeUserId, Id<UpcomingOrder> orderId) {
        log.info("Backoffice user {} marking order {} as delivered", backofficeUserId.value(), orderId.value());
        UpcomingOrder order = subscriptions.findOrderById(orderId);
        DeliveredOrder deliveredOrder = order.markAsDelivered();
        subscriptions.save(deliveredOrder);
    }
}
