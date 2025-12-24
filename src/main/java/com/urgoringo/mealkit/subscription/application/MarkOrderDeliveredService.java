package com.urgoringo.mealkit.subscription.application;

import com.urgoringo.mealkit.backoffice.domain.BackofficeUser;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.subscription.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Slf4j
@NullMarked
@Service
@RequiredArgsConstructor
public class MarkOrderDeliveredService {

    private final Subscriptions subscriptions;
    private final Clock clock;

    @Transactional
    public void execute(Id<BackofficeUser> backofficeUserId, Id<Order> orderId) {
        log.info("Backoffice user {} marking order {} as delivered", backofficeUserId.value(), orderId.value());
        
        UpcomingOrder order = subscriptions.findUpcomingOrderById(orderId);
        
        LockedOrder lockedOrder = switch (order) {
            case PendingOrder pendingOrder -> {
                LockedOrder locked = pendingOrder.locked();
                subscriptions.save(locked);
                yield locked;
            }
            case LockedOrder locked -> locked;
        };
        
        DeliveredOrder deliveredOrder = lockedOrder.markAsDelivered(clock);
        subscriptions.save(deliveredOrder);
    }
}
