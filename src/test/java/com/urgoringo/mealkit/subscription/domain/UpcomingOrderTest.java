package com.urgoringo.mealkit.subscription.domain;

import com.urgoringo.mealkit.domain.ValidationException;
import com.urgoringo.mealkit.scaffolding.TestClock;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.urgoringo.mealkit.scaffolding.TestFactory.anUpcomingOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

class UpcomingOrderTest {

    private final TestClock clock = new TestClock();

    @Test
    void deliveryDateCanBeToday() {
        LocalDate today = LocalDate.of(2025, 12, 23);
        clock.frozenOn(today);
        UpcomingOrder order = anUpcomingOrder()
            .withDeliveryDate(today)
            .build();

        order.markAsDelivered(clock);
    }

    @Test
    void deliveryDateCanBeInPast() {
        LocalDate today = LocalDate.of(2025, 12, 23);
        clock.frozenOn(today);
        LocalDate pastDate = LocalDate.of(2025, 12, 20);
        UpcomingOrder order = anUpcomingOrder()
            .withDeliveryDate(pastDate)
            .build();

        order.markAsDelivered(clock);
    }

    @Test
    void deliveryDateCannotBeInFuture() {
        LocalDate today = LocalDate.of(2025, 12, 23);
        clock.frozenOn(today);
        LocalDate futureDate = LocalDate.of(2025, 12, 25);
        UpcomingOrder order = anUpcomingOrder()
            .withDeliveryDate(futureDate)
            .build();

        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> order.markAsDelivered(clock)
        );
        
        assertThat(exception.getMessage()).isEqualTo("Cannot deliver order before delivery date");
    }
}
