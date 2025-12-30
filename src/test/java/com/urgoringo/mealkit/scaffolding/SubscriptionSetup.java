package com.urgoringo.mealkit.scaffolding;

import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.util.List;

import static com.urgoringo.mealkit.scaffolding.TestFactory.aSubscription;
import static com.urgoringo.mealkit.subscription.api.SubscriptionController.*;

@RequiredArgsConstructor
public class SubscriptionSetup {

    private final ApplicationRunner app;
    private final String authToken;

    public SubscriptionSetup(ApplicationRunner app, String authToken, TestFactory.SubscriptionBuilder subscription) {
        this.app = app;
        this.authToken = authToken;
        List<Long> recipeIds = subscription.recipeIds().isEmpty() ? app.getRecipes(3) : subscription.recipeIds();
        TestFactory.SubscriptionBuilder subscriptionBuilder = subscription.withRecipeIds(recipeIds);
        app.create(subscriptionBuilder, authToken).expectSuccess();
    }

    public SubscriptionResponse get() {
        return app.getCustomerSubscription(authToken).expectSuccess();
    }

    public UpcomingOrderSetup withNextUpcomingOrder() {
        var subscription = app.getCustomerSubscription(authToken).expectSuccess();
        return new UpcomingOrderSetup(app, authToken, subscription.upcomingOrders().getFirst().id());
    }

}
