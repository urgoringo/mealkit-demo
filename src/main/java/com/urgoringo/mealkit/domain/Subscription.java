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
     * Signs up a customer for a new subscription with the first order.
     *
     * @param customerId the customer ID
     * @param recipeIds the recipe IDs for the first order
     * @param deliveryAddress the delivery address
     * @param deliveryDay the delivery day of the week (optional)
     * @param today the current date (used to calculate delivery date when deliveryDay is specified)
     * @return a new Subscription instance
     */
    public static Subscription signup(
            Id<Customer> customerId,
            List<Id<Recipe>> recipeIds,
            String deliveryAddress,
            @Nullable DayOfWeek deliveryDay,
            LocalDate today
    ) {
        LocalDate deliveryDate = deliveryDay != null ? today.with(next(deliveryDay)) : null;
        Order firstOrder = Order.create(recipeIds, deliveryDate);
        return new Subscription(Id.unassigned(), customerId, List.of(firstOrder), deliveryAddress, deliveryDay);
    }
}
