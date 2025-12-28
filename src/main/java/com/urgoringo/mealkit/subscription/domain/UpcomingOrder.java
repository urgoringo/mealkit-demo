package com.urgoringo.mealkit.subscription.domain;

public sealed interface UpcomingOrder extends Order permits PendingOrder, LockedOrder {

    boolean isLocked();
}
