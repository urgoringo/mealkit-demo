package com.urgoringo.mealkit.subscription.domain;

import java.util.List;

public record SubscriptionUpdateResult(
    Subscription subscription,
    List<Object> domainEvents
) {
    public static SubscriptionUpdateResult of(Subscription subscription) {
        return new SubscriptionUpdateResult(subscription, List.of());
    }

    public static SubscriptionUpdateResult of(Subscription subscription, Object event) {
        return new SubscriptionUpdateResult(subscription, List.of(event));
    }

    public static SubscriptionUpdateResult of(Subscription subscription, List<Object> events) {
        return new SubscriptionUpdateResult(subscription, events);
    }
}
