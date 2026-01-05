package com.urgoringo.mealkit.scaffolding;

import com.urgoringo.mealkit.subscription.api.SubscriptionController.OrderResponse;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;

@RequiredArgsConstructor
public class UpcomingOrderSetup {

    private final ApplicationRunner app;
    private final String authToken;
    private final String upcomingOrderId;
    private boolean isLocked = false;

    public UpcomingOrderSetup locked() {
        if (!isLocked) {
            app.freezeTimeOn(app.getUpcomingOrder(authToken, upcomingOrderId).deliveryDate().minusDays(3));
            isLocked = true;
        }
        return this;
    }

    public UpcomingOrderSetup delivered() {
        if (!isLocked) {
            locked();
        }
        var deliveryDate = app.getUpcomingOrder(authToken, upcomingOrderId).deliveryDate();
        app.freezeTimeOn(deliveryDate);
        app.backoffice().markOrderDelivered(upcomingOrderId);

        return this;
    }

    public OrderResponse get() {
        return app.getUpcomingOrder(authToken, upcomingOrderId);
    }

    public UpcomingOrderSetup deliveryDayChangedTo(DayOfWeek deliveryDay) {
        app.updateUpcomingOrderDeliveryDay(upcomingOrderId, deliveryDay, authToken).expectSuccess();
        return this;
    }
}
