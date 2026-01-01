package com.urgoringo.mealkit.subscription.domain;

import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.domain.NotFound;
import com.urgoringo.mealkit.infra.UpcomingSubscriptionOrders;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.urgoringo.mealkit.jooq.tables.Orders.ORDERS;
import static com.urgoringo.mealkit.jooq.tables.Subscriptions.SUBSCRIPTIONS;

@NullMarked
@Repository
@RequiredArgsConstructor
public class Subscriptions {

    private final DSLContext dsl;

    @Transactional
    public Subscription add(Subscription subscription) {
        dsl.insertInto(SUBSCRIPTIONS)
            .set(SUBSCRIPTIONS.ID, subscription.id().value())
            .set(SUBSCRIPTIONS.CUSTOMER_ID, subscription.customerId().value())
            .set(SUBSCRIPTIONS.DELIVERY_ADDRESS, subscription.deliveryAddress())
            .set(SUBSCRIPTIONS.DELIVERY_DAY, subscription.deliveryDay().name())
            .execute();

        subscription.upcomingOrders().stream().forEach(order -> addOrder(order, subscription.id().value()));

        return subscription;
    }

    @Transactional
    public Subscription update(Subscription subscription) {
        dsl.update(SUBSCRIPTIONS)
            .set(SUBSCRIPTIONS.CUSTOMER_ID, subscription.customerId().value())
            .set(SUBSCRIPTIONS.DELIVERY_ADDRESS, subscription.deliveryAddress())
            .set(SUBSCRIPTIONS.DELIVERY_DAY, subscription.deliveryDay().name())
            .where(SUBSCRIPTIONS.ID.eq(subscription.id().value()))
            .execute();

        List<UUID> orderIds = subscription.upcomingOrders().stream()
            .map(order -> order.id().value())
            .toList();

        if (!orderIds.isEmpty()) {
            dsl.deleteFrom(ORDERS)
                .where(ORDERS.SUBSCRIPTION_ID.eq(subscription.id().value())
                    .and(ORDERS.STATUS.ne(OrderStatus.DELIVERED.name()))
                    .and(ORDERS.ID.notIn(orderIds)))
                .execute();
        } else {
            deleteOrdersForSubscription(subscription.id().value());
        }

        subscription.upcomingOrders().stream().forEach(order -> upsertOrder(order, subscription.id().value()));

        return subscription;
    }

    private void addOrder(UpcomingOrder order, UUID subscriptionId) {
        UUID[] recipeIdsArray = order.recipeIds().stream()
            .map(Id::value)
            .toArray(UUID[]::new);

        dsl.insertInto(ORDERS)
            .set(ORDERS.ID, order.id().value())
            .set(ORDERS.SUBSCRIPTION_ID, subscriptionId)
            .set(ORDERS.DELIVERY_DATE, order.deliveryDate())
            .set(ORDERS.STATUS, order.status().name())
            .set(ORDERS.RECIPE_IDS, recipeIdsArray)
            .execute();
    }

    private void upsertOrder(UpcomingOrder order, UUID subscriptionId) {
        UUID[] recipeIdsArray = order.recipeIds().stream()
            .map(Id::value)
            .toArray(UUID[]::new);

        dsl.insertInto(ORDERS)
            .set(ORDERS.ID, order.id().value())
            .set(ORDERS.SUBSCRIPTION_ID, subscriptionId)
            .set(ORDERS.DELIVERY_DATE, order.deliveryDate())
            .set(ORDERS.STATUS, order.status().name())
            .set(ORDERS.RECIPE_IDS, recipeIdsArray)
            .onConflict(ORDERS.ID)
            .doUpdate()
            .set(ORDERS.DELIVERY_DATE, order.deliveryDate())
            .set(ORDERS.STATUS, order.status().name())
            .set(ORDERS.RECIPE_IDS, recipeIdsArray)
            .execute();
    }

    private void updateOrder(UpcomingOrder order) {
        UUID[] recipeIdsArray = order.recipeIds().stream()
            .map(Id::value)
            .toArray(UUID[]::new);

        dsl.update(ORDERS)
            .set(ORDERS.DELIVERY_DATE, order.deliveryDate())
            .set(ORDERS.STATUS, order.status().name())
            .set(ORDERS.RECIPE_IDS, recipeIdsArray)
            .where(ORDERS.ID.eq(order.id().value()))
            .execute();
    }

    private void deleteOrdersForSubscription(UUID subscriptionId) {
        dsl.deleteFrom(ORDERS)
            .where(ORDERS.SUBSCRIPTION_ID.eq(subscriptionId)
                .and(ORDERS.STATUS.ne(OrderStatus.DELIVERED.name())))
            .execute();
    }

    public Subscription findById(Id<Subscription> id) {
        return toSubscription(dsl.selectFrom(SUBSCRIPTIONS)
            .where(SUBSCRIPTIONS.ID.eq(id.value()))
            .fetchSingle());
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
                .and(ORDERS.DELIVERY_DATE.lessOrEqual(maxDeliveryDate)))
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
            UpcomingSubscriptionOrders.of(orders),
            record.getDeliveryAddress(),
            DayOfWeek.valueOf(record.getDeliveryDay())
        );
    }

    private List<UpcomingOrder> fetchOrdersForSubscription(UUID subscriptionId) {
        return dsl.selectFrom(ORDERS)
            .where(ORDERS.SUBSCRIPTION_ID.eq(subscriptionId)
                .and(ORDERS.STATUS.ne(OrderStatus.DELIVERED.name())))
            .fetch()
            .map(orderRecord -> {
                UUID[] recipeIdsArray = orderRecord.getRecipeIds();
                List<Id<Recipe>> recipeIds = Arrays.stream(recipeIdsArray)
                    .map(Id::<Recipe>of)
                    .toList();
                return switch (OrderStatus.valueOf(orderRecord.getStatus())) {
                    case PENDING ->
                        new PendingOrder(Id.of(orderRecord.getId()), recipeIds, orderRecord.getDeliveryDate());
                    case LOCKED ->
                        new LockedOrder(Id.of(orderRecord.getId()), recipeIds, orderRecord.getDeliveryDate());
                    case DELIVERED ->
                        throw new IllegalArgumentException("Invalid order status: " + orderRecord.getStatus());
                };
            });
    }

    @Transactional
    public void markOrderAsDelivered(Id<PendingOrder> orderId) {
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
                UUID orderId = orderRecord.get(ORDERS.ID);
                UUID[] recipeIdsArray = orderRecord.get(ORDERS.RECIPE_IDS);
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

    public LockedOrder findLockedOrderById(Id<Order> orderId) {
        return dsl.selectFrom(ORDERS)
            .where(ORDERS.ID.eq(orderId.value())
                .and(ORDERS.STATUS.eq(OrderStatus.LOCKED.name())))
            .fetchOptional()
            .map(orderRecord -> {
                UUID[] recipeIdsArray = orderRecord.getRecipeIds();
                List<Id<Recipe>> recipeIds = Arrays.stream(recipeIdsArray)
                    .map(Id::<Recipe>of)
                    .toList();
                return new LockedOrder(
                    Id.of(orderRecord.getId()),
                    recipeIds,
                    orderRecord.getDeliveryDate()
                );
            })
            .orElseThrow(() -> new NotFound("Locked order not found: " + orderId.value()));
    }

    @Transactional
    public void updatePendingOrder(PendingOrder order) {
        UUID[] recipeIdsArray = order.recipeIds().stream()
            .map(Id::value)
            .toArray(UUID[]::new);

        dsl.update(ORDERS)
            .set(ORDERS.DELIVERY_DATE, order.deliveryDate())
            .set(ORDERS.STATUS, OrderStatus.PENDING.name())
            .set(ORDERS.RECIPE_IDS, recipeIdsArray)
            .where(ORDERS.ID.eq(order.id().value()))
            .execute();
    }

    @Transactional
    public void updateLockedOrder(LockedOrder order) {
        UUID[] recipeIdsArray = order.recipeIds().stream()
            .map(Id::value)
            .toArray(UUID[]::new);

        dsl.update(ORDERS)
            .set(ORDERS.DELIVERY_DATE, order.deliveryDate())
            .set(ORDERS.STATUS, OrderStatus.LOCKED.name())
            .set(ORDERS.RECIPE_IDS, recipeIdsArray)
            .where(ORDERS.ID.eq(order.id().value()))
            .execute();
    }

    @Transactional
    public void updateDeliveredOrder(DeliveredOrder order) {
        UUID[] recipeIdsArray = order.recipeIds().stream()
            .map(Id::value)
            .toArray(UUID[]::new);

        dsl.update(ORDERS)
            .set(ORDERS.DELIVERY_DATE, order.deliveryDate())
            .set(ORDERS.STATUS, OrderStatus.DELIVERED.name())
            .set(ORDERS.RECIPE_IDS, recipeIdsArray)
            .where(ORDERS.ID.eq(order.id().value()))
            .execute();
    }

    public UpcomingOrder findUpcomingOrderBy(Id<Customer> customerId, Id<Order> orderId) {
        var orderRecord = dsl.select(ORDERS.fields())
            .from(ORDERS)
            .join(SUBSCRIPTIONS).on(SUBSCRIPTIONS.ID.eq(ORDERS.SUBSCRIPTION_ID))
            .where(SUBSCRIPTIONS.CUSTOMER_ID.eq(customerId.value())
                .and(ORDERS.ID.eq(orderId.value())))
            .fetchOptional();

        if (orderRecord.isEmpty()) {
            throw new NotFound("Upcoming order not found: " + orderId.value());
        }

        var record = orderRecord.get();
        UUID[] recipeIdsArray = record.get(ORDERS.RECIPE_IDS);
        List<Id<Recipe>> recipeIds = Arrays.stream(recipeIdsArray)
            .map(Id::<Recipe>of)
            .toList();
        return switch (OrderStatus.valueOf(record.get(ORDERS.STATUS))) {
            case PENDING -> new PendingOrder(Id.of(record.get(ORDERS.ID)), recipeIds, record.get(ORDERS.DELIVERY_DATE));
            case LOCKED -> new LockedOrder(Id.of(record.get(ORDERS.ID)), recipeIds, record.get(ORDERS.DELIVERY_DATE));
            case DELIVERED -> throw new IllegalArgumentException("Invalid order status: " + record.get(ORDERS.STATUS));
        };
    }
}