package com.urgoringo.mealkit.subscription.domain;

import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import org.jspecify.annotations.NullMarked;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.TemporalAdjusters.next;

@NullMarked
public record Subscription(
        Id<Subscription> id,
        Id<Customer> customerId,
        List<Order> upcomingOrders,
        String deliveryAddress,
        DayOfWeek deliveryDay
) {
    public static Subscription signup(
            Id<Customer> customerId,
            List<Id<Recipe>> recipeIds,
            String deliveryAddress,
            DayOfWeek deliveryDay,
            LocalDate today
    ) {
        LocalDate deliveryDate = today.with(next(deliveryDay));
        Order firstOrder = Order.placed(recipeIds, deliveryDate);
        return new Subscription(Id.unassigned(), customerId, List.of(firstOrder), deliveryAddress, deliveryDay);
    }

    public Subscription withNewUpcomingOrder(LocalDate currentDate, List<Id<Recipe>> recipeIds) {
        if (upcomingOrders.isEmpty()) {
            return this;
        }

        Order lastOrder = upcomingOrders.getLast();
        if (lastOrder.deliveryDate() == null) {
            return this;
        }

        long daysUntilDelivery = currentDate.until(lastOrder.deliveryDate(), java.time.temporal.ChronoUnit.DAYS);
        if (daysUntilDelivery <= 3) {
            LocalDate nextDeliveryDate = lastOrder.deliveryDate().with(next(deliveryDay));
            Order newOrder = Order.placed(recipeIds, nextDeliveryDate);

            List<Order> updatedOrders = new ArrayList<>(upcomingOrders);
            updatedOrders.add(newOrder);

            return new Subscription(id, customerId, updatedOrders, deliveryAddress, deliveryDay);
        }

        return this;
    }
}
