package com.urgoringo.mealkit.domain;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Subscription domain model (aggregate root).
 * Contains the full Order objects as part of the aggregate.
 */
@NullMarked
public record Subscription(
        Id<Subscription> id,
        Id<Customer> customerId,
        List<Order> upcomingOrders,
        @Nullable String deliveryAddress
) {
    /**
     * Creates a new Subscription with the first order.
     *
     * @param customerId the customer ID
     * @param firstOrder the first upcoming order
     * @param deliveryAddress the delivery address (optional)
     * @return a new Subscription instance
     */
    public static Subscription create(Id<Customer> customerId, Order firstOrder, @Nullable String deliveryAddress) {
        return new Subscription(Id.unassigned(), customerId, List.of(firstOrder), deliveryAddress);
    }
}
