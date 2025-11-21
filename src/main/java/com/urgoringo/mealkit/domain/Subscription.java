package com.urgoringo.mealkit.domain;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
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

    /**
     * Processes orders for this subscription based on the current date.
     * Adds a new order if we're within the preselection window before the last order's delivery.
     *
     * @param currentDate the current date
     * @param recipeIds the recipe IDs to include in the new order (preselected)
     * @return a new Subscription with updated orders, or the same subscription if no changes needed
     */
    public Subscription processOrders(LocalDate currentDate, List<Id<Recipe>> recipeIds) {
        if (deliveryDay == null || upcomingOrders.isEmpty()) {
            return this;
        }

        // Get the last upcoming order's delivery date
        Order lastOrder = upcomingOrders.getLast();
        if (lastOrder.deliveryDate() == null) {
            return this;
        }

        // If we're 3 or fewer days before the last order's delivery, add a new order
        long daysUntilDelivery = currentDate.until(lastOrder.deliveryDate(), java.time.temporal.ChronoUnit.DAYS);
        if (daysUntilDelivery <= 3) {
            // Calculate next delivery date (one week after the last order)
            LocalDate nextDeliveryDate = lastOrder.deliveryDate().with(next(deliveryDay));
            Order newOrder = Order.create(recipeIds, nextDeliveryDate);

            // Create new orders list with the new order appended
            List<Order> updatedOrders = new ArrayList<>(upcomingOrders);
            updatedOrders.add(newOrder);

            return new Subscription(id, customerId, updatedOrders, deliveryAddress, deliveryDay);
        }

        return this;
    }
}
