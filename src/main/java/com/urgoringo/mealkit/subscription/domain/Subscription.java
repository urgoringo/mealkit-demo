package com.urgoringo.mealkit.subscription.domain;

import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.domain.ValidationFailed;
import com.urgoringo.mealkit.infra.UpcomingOrderList;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import org.jspecify.annotations.NullMarked;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.util.Iterator;
import java.util.List;

import static java.time.temporal.TemporalAdjusters.next;

@NullMarked
public record Subscription(
    Id<Subscription> id,
    Id<Customer> customerId,
    UpcomingOrderList upcomingOrders,
    String deliveryAddress,
    DayOfWeek deliveryDay
) {

    private static final Period PROCESSING_BEFORE_DELIVERY = Period.ofDays(3);

    public static Subscription signup(
        Id<Customer> customerId,
        List<Id<Recipe>> recipeIds,
        String deliveryAddress,
        DayOfWeek deliveryDay,
        LocalDate today
    ) {
        LocalDate deliveryDate = today.plusDays(3).with(next(deliveryDay));
        PendingOrder firstOrder = PendingOrder.placed(recipeIds, deliveryDate);
        return new Subscription(Id.generate(), customerId, UpcomingOrderList.initial(firstOrder), deliveryAddress, deliveryDay);
    }

    private LocalDate nextUpcomingOrderDeliveryDate() {
        UpcomingOrder lastOrder = upcomingOrders.getLast();
        return lastOrder.deliveryDate().with(next(deliveryDay));
    }

    public Subscription withUpdatedRecipes(Id<UpcomingOrder> orderId, List<Id<Recipe>> recipeIds) {
        UpcomingOrderList updatedOrders = upcomingOrders.withUpdatedRecipes(orderId, recipeIds);
        return new Subscription(id, customerId, updatedOrders, deliveryAddress, deliveryDay);
    }

    public Subscription withNewUpcomingOrder(List<Id<Recipe>> recipeIds) {
        if (upcomingOrders.size() >= 2) {
            return this;
        }

        LocalDate nextDeliveryDate = nextUpcomingOrderDeliveryDate();
        PendingOrder newOrder = PendingOrder.placed(recipeIds, nextDeliveryDate);

        var updatedOrders = upcomingOrders.with(newOrder);
        return new Subscription(id, customerId, updatedOrders, deliveryAddress, deliveryDay);
    }

    public Subscription withLockedUpcomingOrder(Clock clock) {
        UpcomingOrder nextOrder = upcomingOrders.getFirst();

        return switch (nextOrder) {
            case PendingOrder pendingOrder -> {
                if (pendingOrder.shouldBeLocked(clock)) {
                    yield new Subscription(id,
                        customerId,
                        upcomingOrders.with(pendingOrder.locked()),
                        deliveryAddress,
                        deliveryDay);
                }
                yield this;
            }
            case LockedOrder _ -> this;
        };
    }

    public LocalDate nextProcessingDate() {
        UpcomingOrder nextPendingOrder = upcomingOrders.nextPendingOrder();
        return nextPendingOrder.deliveryDate().minus(PROCESSING_BEFORE_DELIVERY);
    }

    public Subscription withUpdatedDeliveryDay(DayOfWeek newDeliveryDay, Clock clock) {
        return new Subscription(id,
            customerId,
            upcomingOrders.withUpdatedDeliveryDay(deliveryDay, newDeliveryDay),
            deliveryAddress,
            newDeliveryDay);
    }

    public Subscription withUpdatedOrderDeliveryDate(Id<UpcomingOrder> orderId, DayOfWeek newDeliveryDay) {
        UpcomingOrderList updatedOrders = upcomingOrders.withUpdatedOrderDeliveryDate(orderId, newDeliveryDay);
        return new Subscription(id, customerId, updatedOrders, deliveryAddress, deliveryDay);
    }

}
