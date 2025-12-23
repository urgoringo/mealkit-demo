package com.urgoringo.mealkit.scaffolding;

import com.urgoringo.mealkit.subscription.api.SubscriptionController;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static com.urgoringo.mealkit.scaffolding.TestFactory.aSubscription;
import static com.urgoringo.mealkit.subscription.api.SubscriptionController.*;

@RequiredArgsConstructor
public class SubscriptionSetup {

    private final ApplicationRunner app;
    private final String authToken;

    public SubscriptionSetup(ApplicationRunner app, String authToken, DayOfWeek deliveryDay) {
        this.app = app;
        this.authToken = authToken;
        app.create(
            aSubscription()
                .withRecipeIds(app.getRecipes(3))
                .withDeliveryDay(deliveryDay), authToken
        ).expectSuccess();
    }

    public SubscriptionResponse get() {
        return app.getCustomerSubscription(authToken).expectSuccess();
    }

    public SubscriptionSetup withOrderLocked() {
        var subscription = app.getCustomerSubscription(authToken).expectSuccess();
        var deliveryDate = subscription.upcomingOrders().getFirst().deliveryDate();
        app.freezeTimeOn(deliveryDate.minusDays(3));
        return this;
    }

    public void withOrderDelivered() {
        var subscription = app.getCustomerSubscription(authToken).expectSuccess();
        var deliveryDate = subscription.upcomingOrders().getFirst().deliveryDate();
        app.freezeTimeOn(deliveryDate.minusDays(3));
        app.processSubscriptionOrders();
        app.freezeTimeOn(deliveryDate);
        
        var updatedSubscription = app.getCustomerSubscription(authToken).expectSuccess();
        var orderToDeliver = updatedSubscription.upcomingOrders().stream()
                .filter(order -> order.deliveryDate().equals(deliveryDate))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Order with delivery date " + deliveryDate + " not found"));
        
        app.backoffice().markOrderDelivered(orderToDeliver.id());
    }
}
