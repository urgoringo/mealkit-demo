package com.urgoringo.mealkit.subscription.domain;

import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.domain.ValidationFailed;
import com.urgoringo.mealkit.infra.OrderList;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import org.jspecify.annotations.NullMarked;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static java.time.temporal.TemporalAdjusters.next;

@NullMarked
public record Subscription(
    Id<Subscription> id,
    Id<Customer> customerId,
    List<UpcomingOrder> upcomingOrders,
    String deliveryAddress,
    DayOfWeek deliveryDay
) {
    public Subscription {
        if (upcomingOrders.isEmpty()) {
            throw new ValidationFailed("Subscription must have at least one upcoming order");
        }
//        upcomingOrders = new TreeSet<>(upcomingOrders, Comparator.comparing(UpcomingOrder::deliveryDate));
    }

    public static Subscription signup(
        Id<Customer> customerId,
        List<Id<Recipe>> recipeIds,
        String deliveryAddress,
        DayOfWeek deliveryDay,
        LocalDate today
    ) {
        LocalDate deliveryDate = today.with(next(deliveryDay));
        PendingOrder firstOrder = PendingOrder.placed(recipeIds, deliveryDate);
        return new Subscription(Id.unassigned(), customerId, List.of(firstOrder), deliveryAddress, deliveryDay);
    }

    private LocalDate nextUpcomingOrderDeliveryDate() {
        UpcomingOrder lastOrder = upcomingOrders.getLast();
        return lastOrder.deliveryDate().with(next(deliveryDay));
    }

    public Subscription withUpdatedRecipes(Id<UpcomingOrder> orderId, List<Id<Recipe>> recipeIds) {
        List<UpcomingOrder> updatedOrders = upcomingOrders.stream()
            .map(order -> {
                if (order.id().equals(orderId)) {
                    return switch (order) {
                        case PendingOrder pendingOrder -> pendingOrder.withUpdatedRecipes(recipeIds);
                        case LockedOrder _ -> throw new ValidationFailed("Cannot update locked order");
                    };
                }
                return order;
            })
            .toList();

        return new Subscription(id, customerId, updatedOrders, deliveryAddress, deliveryDay);
    }

    public Subscription withNewUpcomingOrder(List<Id<Recipe>> recipeIds) {
        if (upcomingOrders.isEmpty()) {
            return this;
        }

        if (upcomingOrders.size() >= 2) {
            return this;
        }

        LocalDate nextDeliveryDate = nextUpcomingOrderDeliveryDate();
        PendingOrder newOrder = PendingOrder.placed(recipeIds, nextDeliveryDate);

        var updatedOrders = OrderList.with(upcomingOrders, newOrder);
        return new Subscription(id, customerId, updatedOrders, deliveryAddress, deliveryDay);
    }

    public Subscription withLockedUpcomingOrder(Clock clock) {
        if (upcomingOrders.isEmpty()) {
            return this;
        }
        
        UpcomingOrder nextOrder = upcomingOrders.getFirst();

        return switch (nextOrder) {
            case PendingOrder pendingOrder -> {
                if (pendingOrder.shouldBeLocked(clock)) {
                    List<UpcomingOrder> updatedOrders = upcomingOrders.stream()
                        .map(order -> order.equals(pendingOrder) ? pendingOrder.locked() : order)
                        .toList();
                    yield new Subscription(id, customerId, updatedOrders, deliveryAddress, deliveryDay);
                }
                yield this;
            }
            case LockedOrder _ -> this;
        };
    }
}
