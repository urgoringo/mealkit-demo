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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

            List<Id<UpcomingOrder>> existingOrderIds = subscription.upcomingOrders().stream()
                .map(UpcomingOrder::id)
                .filter(Id::isAssigned)
                .toList();

            if (!existingOrderIds.isEmpty()) {
                List<Long> existingOrderIdValues = existingOrderIds.stream()
                    .map(Id::value)
                    .toList();
                List<Long> orderIdsToDelete = dsl.select(ORDERS.ID)
                    .from(ORDERS)
                    .where(ORDERS.SUBSCRIPTION_ID.eq(subscriptionId)
                        .and(ORDERS.STATUS.ne(OrderStatus.DELIVERED.name()))
                        .and(ORDERS.ID.notIn(existingOrderIdValues)))
                    .fetch(ORDERS.ID);

                if (!orderIdsToDelete.isEmpty()) {
                    dsl.deleteFrom(ORDERS)
                        .where(ORDERS.ID.in(orderIdsToDelete))
                        .execute();
                }
            } else {
                deleteOrdersForSubscription(subscriptionId);
            }
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
            UpcomingOrder savedOrder = saveOrUpdateOrder(order, subscriptionId);
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

    private UpcomingOrder saveOrUpdateOrder(UpcomingOrder order, Long subscriptionId) {
        Long[] recipeIdsArray = order.recipeIds().stream()
            .map(Id::value)
            .toArray(Long[]::new);

        if (order.id().isAssigned()) {
            Long orderId = order.id().value();
            dsl.update(ORDERS)
                .set(ORDERS.DELIVERY_DATE, order.deliveryDate())
                .set(ORDERS.STATUS, order.status().name())
                .set(ORDERS.RECIPE_IDS, recipeIdsArray)
                .where(ORDERS.ID.eq(orderId))
                .execute();

            return order;
        } else {
            var orderRecord = dsl.insertInto(ORDERS)
                .set(ORDERS.SUBSCRIPTION_ID, subscriptionId)
                .set(ORDERS.DELIVERY_DATE, order.deliveryDate())
                .set(ORDERS.STATUS, order.status().name())
                .set(ORDERS.RECIPE_IDS, recipeIdsArray)
                .returning(ORDERS.ID)
                .fetchOne();
            if (orderRecord == null) {
                throw new IllegalStateException("Failed to insert order");
            }
            Long orderId = orderRecord.getId();

            return new UpcomingOrder(Id.of(orderId), order.recipeIds(), order.deliveryDate(), order.status());
        }
    }

    private void deleteOrdersForSubscription(Long subscriptionId) {
        dsl.deleteFrom(ORDERS)
            .where(ORDERS.SUBSCRIPTION_ID.eq(subscriptionId)
                .and(ORDERS.STATUS.ne(OrderStatus.DELIVERED.name())))
            .execute();
    }

    public Optional<Subscription> findById(Id<Subscription> id) {
        return dsl.selectFrom(SUBSCRIPTIONS)
            .where(SUBSCRIPTIONS.ID.eq(id.value()))
            .fetchOptional()
            .map(this::toSubscription);
    }

    public Subscription findByCustomerId(Id<Customer> customerId) {
        return toSubscription(dsl.selectFrom(SUBSCRIPTIONS)
            .where(SUBSCRIPTIONS.CUSTOMER_ID.eq(customerId.value()))
            .fetchSingle());
    }

    public List<Id<Subscription>> findAllIds() {
        return dsl.select(SUBSCRIPTIONS.ID)
            .from(SUBSCRIPTIONS)
            .fetch(SUBSCRIPTIONS.ID)
            .stream()
            .map(Id::<Subscription>of)
            .toList();
    }

    public List<Id<Subscription>> findSubscriptionsWithPendingOrdersByDeliveryDate(LocalDate maxDeliveryDate) {
        return dsl.selectDistinct(ORDERS.SUBSCRIPTION_ID)
            .from(ORDERS)
            .where(ORDERS.STATUS.eq(OrderStatus.PENDING.name())
                .and(ORDERS.DELIVERY_DATE.le(maxDeliveryDate)))
            .fetch(ORDERS.SUBSCRIPTION_ID)
            .stream()
            .map(Id::<Subscription>of)
            .toList();
    }

    @Transactional
    public void deleteAll() {
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
            .where(ORDERS.SUBSCRIPTION_ID.eq(subscriptionId)
                .and(ORDERS.STATUS.ne(OrderStatus.DELIVERED.name())))
            .fetch()
            .map(orderRecord -> {
                Long[] recipeIdsArray = orderRecord.getRecipeIds();
                List<Id<Recipe>> recipeIds = Arrays.stream(recipeIdsArray)
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

    public List<DeliveredOrder> findDeliveredOrdersByCustomerId(Id<Customer> customerId) {
        return dsl.select(ORDERS.fields())
            .from(ORDERS)
            .join(SUBSCRIPTIONS).on(SUBSCRIPTIONS.ID.eq(ORDERS.SUBSCRIPTION_ID))
            .where(SUBSCRIPTIONS.CUSTOMER_ID.eq(customerId.value())
                .and(ORDERS.STATUS.eq(OrderStatus.DELIVERED.name())))
            .fetch()
            .map(orderRecord -> {
                Long orderId = orderRecord.get(ORDERS.ID);
                Long[] recipeIdsArray = orderRecord.get(ORDERS.RECIPE_IDS);
                List<Id<Recipe>> recipeIds = Arrays.stream(recipeIdsArray)
                    .map(Id::<Recipe>of)
                    .toList();
                return new DeliveredOrder(
                    Id.of(orderId),
                    recipeIds,
                    orderRecord.get(ORDERS.DELIVERY_DATE)
                );
            });
    }

    public UpcomingOrder findOrderById(Id<UpcomingOrder> orderId) {
        return dsl.selectFrom(ORDERS)
            .where(ORDERS.ID.eq(orderId.value()))
            .fetchOptional()
            .map(orderRecord -> {
                Long[] recipeIdsArray = orderRecord.getRecipeIds();
                List<Id<Recipe>> recipeIds = Arrays.stream(recipeIdsArray)
                    .map(Id::<Recipe>of)
                    .toList();
                OrderStatus status = OrderStatus.valueOf(orderRecord.getStatus());
                return new UpcomingOrder(
                    Id.of(orderRecord.getId()),
                    recipeIds,
                    orderRecord.getDeliveryDate(),
                    status
                );
            })
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId.value()));
    }

    @Transactional
    public void save(UpcomingOrder order) {
        if (!order.id().isAssigned()) {
            throw new IllegalArgumentException("Cannot save order without an assigned ID");
        }

        Long[] recipeIdsArray = order.recipeIds().stream()
            .map(Id::value)
            .toArray(Long[]::new);

        dsl.update(ORDERS)
            .set(ORDERS.DELIVERY_DATE, order.deliveryDate())
            .set(ORDERS.STATUS, OrderStatus.PENDING.name())
            .set(ORDERS.RECIPE_IDS, recipeIdsArray)
            .where(ORDERS.ID.eq(order.id().value()))
            .execute();
    }

    @Transactional
    public void save(DeliveredOrder order) {
        if (!order.id().isAssigned()) {
            throw new IllegalArgumentException("Cannot save order without an assigned ID");
        }

        Long[] recipeIdsArray = order.recipeIds().stream()
            .map(Id::value)
            .toArray(Long[]::new);

        dsl.update(ORDERS)
            .set(ORDERS.DELIVERY_DATE, order.deliveryDate())
            .set(ORDERS.STATUS, OrderStatus.DELIVERED.name())
            .set(ORDERS.RECIPE_IDS, recipeIdsArray)
            .where(ORDERS.ID.eq(order.id().value()))
            .execute();
    }

    @Transactional
    public void lockOrder(Id<UpcomingOrder> orderId) {
        dsl.update(ORDERS)
            .set(ORDERS.STATUS, OrderStatus.LOCKED.name())
            .where(ORDERS.ID.eq(orderId.value()))
            .execute();
    }
}
