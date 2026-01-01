package com.urgoringo.mealkit.infra;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.domain.ValidationFailed;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import com.urgoringo.mealkit.subscription.domain.*;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static java.time.temporal.TemporalAdjusters.next;

public class UpcomingOrderList {
    private final List<UpcomingOrder> items;

    public UpcomingOrderList(List<UpcomingOrder> items) {
        if (items.isEmpty()) {
            throw new ValidationFailed("Subscription must have at least one upcoming order");
        }
        this.items = items;
    }

    public static UpcomingOrderList initial(PendingOrder firstOrder) {
        return new UpcomingOrderList(List.of(firstOrder));
    }

    public static UpcomingOrderList of(List<UpcomingOrder> orders) {
        return new UpcomingOrderList(orders.stream().sorted(Comparator.comparing(UpcomingOrder::deliveryDate)).toList());
    }

    public UpcomingOrderList with(UpcomingOrder orderToAddOrReplace) {
        List<UpcomingOrder> result = new ArrayList<>(items);
        result.removeIf(existingOrder -> existingOrder.id().equals(orderToAddOrReplace.id()));
        result.add(orderToAddOrReplace);
        return new UpcomingOrderList(result.stream().sorted(Comparator.comparing(UpcomingOrder::deliveryDate)).toList());
    }

    public UpcomingOrder nextPendingOrder() {
        return items.stream()
            .filter(order -> !order.isLocked())
            .findFirst()
            .orElseThrow();
    }

    public UpcomingOrder getLast() {
        return items.getLast();
    }

    public Stream<UpcomingOrder> stream() {
        return items.stream();
    }

    public int size() {
        return items.size();
    }

    public UpcomingOrder getFirst() {
        return items.getFirst();
    }

    public UpcomingOrderList withUpdatedDeliveryDay(DayOfWeek oldSubscriptionDeliveryDay, DayOfWeek newSubscriptionDeliveryDay) {
        return new UpcomingOrderList(items.stream()
            .map(order -> {
                if (order.isLocked()) {
                    return order;
                }

                if (order.deliveryDate().getDayOfWeek() != oldSubscriptionDeliveryDay) {
                    return order;
                }

                LocalDate newDeliveryDate = order.deliveryDate().with(next(newSubscriptionDeliveryDay));
                return switch (order) {
                    case PendingOrder pendingOrder -> pendingOrder.withUpdatedDeliveryDate(newDeliveryDate);
                    case LockedOrder lockedOrder -> lockedOrder;
                };
            })
            .toList());
    }

    public UpcomingOrderList withUpdatedOrderDeliveryDate(Id<UpcomingOrder> orderId, DayOfWeek newDeliveryDay) {
        UpcomingOrder order = findItem(orderId);

        switch (order) {
            case PendingOrder pendingOrder ->
                pendingOrder.withUpdatedDeliveryDate(order.deliveryDate().with(next(newDeliveryDay)));
            case LockedOrder _ -> throw new ValidationFailed("Cannot update locked order");
        }

        return with(order);
    }

    private UpcomingOrder findItem(Id<UpcomingOrder> orderId) {
        return items.stream()
            .filter(order -> order.id().equals(orderId))
            .findFirst()
            .orElseThrow();
    }

    public UpcomingOrderList withUpdatedRecipes(Id<UpcomingOrder> orderId, List<Id<Recipe>> recipeIds) {
        List<UpcomingOrder> updatedOrders = items.stream()
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

        return new UpcomingOrderList(updatedOrders);
    }
}