package com.urgoringo.mealkit.scaffolding;

import com.urgoringo.mealkit.subscription.api.SubscriptionController;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;

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


}
