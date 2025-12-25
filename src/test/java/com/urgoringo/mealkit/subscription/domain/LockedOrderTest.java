package com.urgoringo.mealkit.subscription.domain;

import com.urgoringo.mealkit.domain.ValidationFailed;
import com.urgoringo.mealkit.scaffolding.TestClock;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.urgoringo.mealkit.scaffolding.TestFactory.anPendingOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LockedOrderTest {

    private final TestClock clock = new TestClock();

    @Test
    void deliveryDateCanBeToday() {
        LocalDate today = LocalDate.of(2025, 12, 23);
        clock.frozenOn(today);
        LockedOrder order = anPendingOrder()
            .withDeliveryDate(today)
            .build()
            .locked();

        order.markAsDelivered(clock);
    }

    @Test
    void deliveryDateCanBeInPast() {
        LocalDate today = LocalDate.of(2025, 12, 23);
        clock.frozenOn(today);
        LocalDate pastDate = LocalDate.of(2025, 12, 20);
        LockedOrder order = anPendingOrder()
            .withDeliveryDate(pastDate)
            .build()
            .locked();

        order.markAsDelivered(clock);
    }

    @Test
    void deliveryDateCannotBeInFuture() {
        LocalDate today = LocalDate.of(2025, 12, 23);
        clock.frozenOn(today);
        LocalDate futureDate = LocalDate.of(2025, 12, 25);
        LockedOrder order = anPendingOrder()
            .withDeliveryDate(futureDate)
            .build()
            .locked();

        ValidationFailed exception = assertThrows(
            ValidationFailed.class,
            () -> order.markAsDelivered(clock)
        );
        
        assertThat(exception.getMessage()).isEqualTo("Cannot deliver order before delivery date");
    }
}
