package com.urgoringo.mealkit.scaffolding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;

import static com.urgoringo.mealkit.scaffolding.TestFactory.*;

@RequiredArgsConstructor
public class SubscriptionSetup {
    private final ApplicationRunner app;
    @Getter
    private String authToken;

    public SubscriptionSetup havingCustomer() {
        authToken = app.signupCustomer(anEmail(), aPassword()).expectSuccess().token();
        return this;
    }

    public SubscriptionSetup subscription(DayOfWeek deliveryDay) {
        if (authToken == null) {
            havingCustomer();
        }

        app.create(
                authToken,
                aSubscription()
                        .withRecipeIds(app.getRecipes(3))
                        .withDeliveryDay(deliveryDay)
        ).expectSuccess();
        return this;
    }

}
