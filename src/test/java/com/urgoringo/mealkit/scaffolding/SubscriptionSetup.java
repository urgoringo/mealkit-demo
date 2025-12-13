package com.urgoringo.mealkit.scaffolding;

import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;

import static com.urgoringo.mealkit.scaffolding.TestFactory.aSubscription;

@RequiredArgsConstructor
public class SubscriptionSetup {

    public SubscriptionSetup(ApplicationRunner app, String authToken, DayOfWeek deliveryDay) {
        app.create(
                authToken,
                aSubscription()
                        .withRecipeIds(app.getRecipes(3))
                        .withDeliveryDay(deliveryDay)
        ).expectSuccess();
    }

}
