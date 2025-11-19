package com.urgoringo.mealkit.domain;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static java.time.temporal.TemporalAdjusters.next;

/**
 * Subscription domain model (aggregate root).
 * Contains the full Order objects as part of the aggregate.
 */
@NullMarked
public record Subscription(
        Id<Subscription> id,
        Id<Customer> customerId,
        List<Order> upcomingOrders,
        String deliveryAddress,
        @Nullable DayOfWeek deliveryDay
) {
    /**
     * Creates a new Subscription with the first order.
     *
     * @param customerId the customer ID
     * @param firstOrder the first upcoming order
     * @param deliveryAddress the delivery address
     * @param deliveryDay the delivery day of the week (optional)
     * @param today the current date (used to calculate delivery date when deliveryDay is specified)
     * @return a new Subscription instance
     */
    public static Subscription create(
            Id<Customer> customerId,
            Order firstOrder,
            String deliveryAddress,
            @Nullable DayOfWeek deliveryDay,
            LocalDate today
    ) {
        // If delivery day is specified, calculate delivery date for the first order
        Order orderWithDeliveryDate = firstOrder;
        if (deliveryDay != null) {
            LocalDate deliveryDate = today.with(next(deliveryDay));
            orderWithDeliveryDate = new Order(firstOrder.id(), firstOrder.recipeIds(), deliveryDate);
        }

        return new Subscription(Id.unassigned(), customerId, List.of(orderWithDeliveryDate), deliveryAddress, deliveryDay);
    }
}
