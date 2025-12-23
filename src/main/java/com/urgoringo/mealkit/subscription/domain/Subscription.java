package com.urgoringo.mealkit.subscription.domain;

import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.domain.ValidationException;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import org.jspecify.annotations.NullMarked;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.*;
import static java.time.temporal.TemporalAdjusters.next;

@NullMarked
public record Subscription(
        Id<Subscription> id,
        Id<Customer> customerId,
        List<UpcomingOrder> upcomingOrders,
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
        UpcomingOrder firstOrder = UpcomingOrder.placed(recipeIds, deliveryDate);
        return new Subscription(Id.unassigned(), customerId, List.of(firstOrder), deliveryAddress, deliveryDay);
    }

    public Subscription withNewUpcomingOrder(LocalDate currentDate, List<Id<Recipe>> recipeIds) {
        if (upcomingOrders.isEmpty()) {
            return this;
        }

        UpcomingOrder lastOrder = upcomingOrders.getLast();

        Duration daysUntilDelivery = Duration.between(currentDate.atStartOfDay(), lastOrder.deliveryDate().atStartOfDay());
        if (daysUntilDelivery.toDays() <= 3) {
            LocalDate nextDeliveryDate = lastOrder.deliveryDate().with(next(deliveryDay));
            UpcomingOrder newOrder = UpcomingOrder.placed(recipeIds, nextDeliveryDate);

            var updatedOrders = upcomingOrdersWithAdditionalOrder(newOrder);

            return new Subscription(id, customerId, updatedOrders, deliveryAddress, deliveryDay);
        }

        return this;
    }

    private List<UpcomingOrder> upcomingOrdersWithAdditionalOrder(UpcomingOrder newOrder) {
        List<UpcomingOrder> updatedOrders = new ArrayList<>(upcomingOrders);
        updatedOrders.add(newOrder);
        return updatedOrders;
    }

    public Subscription withUpdatedRecipesForFirstUpcomingOrder(List<Id<Recipe>> recipeIds) {
        if (upcomingOrders.isEmpty()) {
            return this;
        }

        UpcomingOrder firstOrder = upcomingOrders.getFirst();
        UpcomingOrder updatedOrder = firstOrder.withUpdatedRecipes(recipeIds);

        return new Subscription(id, customerId, List.of(updatedOrder), deliveryAddress, deliveryDay);
    }

    public Subscription withUpdatedRecipes(Id<UpcomingOrder> orderId, List<Id<Recipe>> recipeIds, Clock clock) {
        UpcomingOrder orderToUpdate = upcomingOrders.stream()
                .filter(order -> order.id().equals(orderId))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Order not found"));
        
        if (orderToUpdate.status(clock) == OrderStatus.LOCKED) {
            throw new ValidationException("Cannot update locked order");
        }
        
        List<UpcomingOrder> updatedOrders = upcomingOrders.stream()
                .map(order -> order.id().equals(orderId) ? order.withUpdatedRecipes(recipeIds) : order)
                .toList();

        return new Subscription(id, customerId, updatedOrders, deliveryAddress, deliveryDay);
    }

    public List<Id<UpcomingOrder>> ordersToLock(Clock clock) {
        LocalDate currentDate = LocalDate.now(clock);
        return upcomingOrders.stream()
                .filter(order -> order.status(clock) == OrderStatus.LOCKED)
                .filter(order -> order.id().isAssigned())
                .map(UpcomingOrder::id)
                .toList();
    }

    public SubscriptionUpdate withLockedOrdersAndNewUpcomingOrder(Clock clock, List<Id<Recipe>> recipeIds) {
        List<Id<UpcomingOrder>> ordersToLock = ordersToLock(clock);
        
        LocalDate currentDate = LocalDate.now(clock);
        Subscription updatedSubscription = withNewUpcomingOrder(currentDate, recipeIds);
        
        return new SubscriptionUpdate(updatedSubscription, ordersToLock);
    }

    public record SubscriptionUpdate(
        Subscription subscription,
        List<Id<UpcomingOrder>> ordersToLock
    ) {
        public boolean hasChanges() {
            return !ordersToLock.isEmpty();
        }
    }

}
