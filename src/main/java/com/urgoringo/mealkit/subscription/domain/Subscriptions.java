package com.urgoringo.mealkit.subscription.domain;

import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.urgoringo.mealkit.jooq.tables.OrderRecipes.ORDER_RECIPES;
import static com.urgoringo.mealkit.jooq.tables.Orders.ORDERS;
import static com.urgoringo.mealkit.jooq.tables.Subscriptions.SUBSCRIPTIONS;

@NullMarked
@Repository
@RequiredArgsConstructor
public class Subscriptions {

    private final DSLContext dsl;

    @Transactional
    public Subscription save(Subscription subscription) {
        Long subscriptionId;

        if (subscription.id().isAssigned()) {
            subscriptionId = subscription.id().value();
            dsl.update(SUBSCRIPTIONS)
                    .set(SUBSCRIPTIONS.CUSTOMER_ID, subscription.customerId().value())
                    .set(SUBSCRIPTIONS.DELIVERY_ADDRESS, subscription.deliveryAddress())
                    .set(SUBSCRIPTIONS.DELIVERY_DAY, subscription.deliveryDay().name())
                    .where(SUBSCRIPTIONS.ID.eq(subscriptionId))
                    .execute();

            deleteOrdersForSubscription(subscriptionId);
        } else {
            var record = dsl.insertInto(SUBSCRIPTIONS)
                    .set(SUBSCRIPTIONS.CUSTOMER_ID, subscription.customerId().value())
                    .set(SUBSCRIPTIONS.DELIVERY_ADDRESS, subscription.deliveryAddress())
                    .set(SUBSCRIPTIONS.DELIVERY_DAY, subscription.deliveryDay().name())
                    .returning(SUBSCRIPTIONS.ID)
                    .fetchOne();
            if (record == null) {
                throw new IllegalStateException("Failed to insert subscription");
            }
            subscriptionId = record.getId();
        }

        List<UpcomingOrder> savedOrders = new ArrayList<>();
        for (UpcomingOrder order : subscription.upcomingOrders()) {
            UpcomingOrder savedOrder = saveOrder(order, subscriptionId);
            savedOrders.add(savedOrder);
        }

        return new Subscription(
                Id.of(subscriptionId),
                subscription.customerId(),
                savedOrders,
                subscription.deliveryAddress(),
                subscription.deliveryDay()
        );
    }

    private UpcomingOrder saveOrder(UpcomingOrder order, Long subscriptionId) {
        var orderRecord = dsl.insertInto(ORDERS)
                .set(ORDERS.SUBSCRIPTION_ID, subscriptionId)
                .set(ORDERS.DELIVERY_DATE, order.deliveryDate())
                .set(ORDERS.STATUS, order.status().name())
                .returning(ORDERS.ID)
                .fetchOne();
        if (orderRecord == null) {
            throw new IllegalStateException("Failed to insert order");
        }
        Long orderId = orderRecord.getId();

        for (Id<Recipe> recipeId : order.recipeIds()) {
            dsl.insertInto(ORDER_RECIPES)
                    .set(ORDER_RECIPES.ORDER_ID, orderId)
                    .set(ORDER_RECIPES.RECIPE_ID, recipeId.value())
                    .execute();
        }

        return new UpcomingOrder(Id.of(orderId), order.recipeIds(), order.deliveryDate(), order.status());
    }

    private void deleteOrdersForSubscription(Long subscriptionId) {
        List<Long> orderIds = dsl.select(ORDERS.ID)
                .from(ORDERS)
                .where(ORDERS.SUBSCRIPTION_ID.eq(subscriptionId))
                .fetch(ORDERS.ID);

        if (!orderIds.isEmpty()) {
            dsl.deleteFrom(ORDER_RECIPES)
                    .where(ORDER_RECIPES.ORDER_ID.in(orderIds))
                    .execute();
        }

        dsl.deleteFrom(ORDERS)
                .where(ORDERS.SUBSCRIPTION_ID.eq(subscriptionId))
                .execute();
    }

    public Optional<Subscription> findById(Id<Subscription> id) {
        return dsl.selectFrom(SUBSCRIPTIONS)
                .where(SUBSCRIPTIONS.ID.eq(id.value()))
                .fetchOptional()
                .map(this::toSubscription);
    }

    public Optional<Subscription> findByCustomerId(Id<Customer> customerId) {
        return dsl.selectFrom(SUBSCRIPTIONS)
                .where(SUBSCRIPTIONS.CUSTOMER_ID.eq(customerId.value()))
                .fetchOptional()
                .map(this::toSubscription);
    }

    public List<Id<Subscription>> findAllIds() {
        return dsl.select(SUBSCRIPTIONS.ID)
                .from(SUBSCRIPTIONS)
                .fetch(SUBSCRIPTIONS.ID)
                .stream()
                .map(Id::<Subscription>of)
                .toList();
    }

    @Transactional
    public void deleteAll() {
        dsl.deleteFrom(ORDER_RECIPES).execute();
        dsl.deleteFrom(ORDERS).execute();
        dsl.deleteFrom(SUBSCRIPTIONS).execute();
    }

    private Subscription toSubscription(com.urgoringo.mealkit.jooq.tables.records.SubscriptionsRecord record) {
        List<UpcomingOrder> orders = fetchOrdersForSubscription(record.getId());
        return new Subscription(
                Id.of(record.getId()),
                Id.of(record.getCustomerId()),
                orders,
                record.getDeliveryAddress(),
                DayOfWeek.valueOf(record.getDeliveryDay())
        );
    }

    private List<UpcomingOrder> fetchOrdersForSubscription(Long subscriptionId) {
        return dsl.selectFrom(ORDERS)
                .where(ORDERS.SUBSCRIPTION_ID.eq(subscriptionId))
                .fetch()
                .map(orderRecord -> {
                    List<Id<Recipe>> recipeIds = dsl.select(ORDER_RECIPES.RECIPE_ID)
                            .from(ORDER_RECIPES)
                            .where(ORDER_RECIPES.ORDER_ID.eq(orderRecord.getId()))
                            .fetch(ORDER_RECIPES.RECIPE_ID)
                            .stream()
                            .map(Id::<Recipe>of)
                            .toList();
                    OrderStatus status = OrderStatus.valueOf(orderRecord.getStatus());
                    return new UpcomingOrder(Id.of(orderRecord.getId()), recipeIds, orderRecord.getDeliveryDate(), status);
                });
    }

    @Transactional
    public void markOrderAsDelivered(Id<UpcomingOrder> orderId) {
        dsl.update(ORDERS)
                .set(ORDERS.STATUS, OrderStatus.DELIVERED.name())
                .where(ORDERS.ID.eq(orderId.value()))
                .execute();
    }
}
