package com.urgoringo.mealkit.scaffolding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.util.List;

import static com.urgoringo.mealkit.scaffolding.ApplicationRunner.SubscriptionRequest.aSubscription;
import static com.urgoringo.mealkit.scaffolding.TestFactory.aPassword;
import static com.urgoringo.mealkit.scaffolding.TestFactory.anEmail;
import static java.time.DayOfWeek.WEDNESDAY;

@RequiredArgsConstructor
public class SubscriptionSetup {
    private final ApplicationRunner app;
    @Getter
    private String authToken;
    private ApplicationRunner.SubscriptionResponse subscription;

    public SubscriptionSetup havingCustomer() {
        authToken = app.signupCustomer(anEmail(), aPassword()).expectSuccess().token();
        return this;
    }

    public SubscriptionSetup subscription(DayOfWeek deliveryDay) {
        if (authToken == null) {
            havingCustomer();
        }
        List<Long> recipeIds = app.getAllRecipes().subList(0, 3).stream()
                .map(ApplicationRunner.RecipeResponse::id)
                .toList();

        subscription = app.create(
                authToken,
                aSubscription(recipeIds).withDeliveryDay(deliveryDay)
        ).expectSuccess();
        return this;
    }

    public SubscriptionSetup subscription() {
        subscription(WEDNESDAY);
        return this;
    }

    public ApplicationRunner.SubscriptionResponse get() {
        if (subscription == null) {
            subscription();
        }
        return app.getCustomerSubscription(authToken).expectSuccess();
    }

}
