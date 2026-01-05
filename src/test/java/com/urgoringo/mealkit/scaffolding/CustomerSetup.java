package com.urgoringo.mealkit.scaffolding;

import lombok.Getter;
import org.jspecify.annotations.NullMarked;

import java.time.DayOfWeek;
import java.util.List;

import static com.urgoringo.mealkit.scaffolding.TestFactory.*;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.WEDNESDAY;

@NullMarked
public class CustomerSetup {
    @Getter
    private final String authToken;
    private final ApplicationRunner app;

    public CustomerSetup(ApplicationRunner app) {
        authToken = app.signupCustomer(anEmail(), aPassword()).expectSuccess().token();
        this.app = app;
    }

    public SubscriptionSetup havingSubscription() {
        return having(aSubscription().withDeliveryDay(WEDNESDAY));
    }

    public SubscriptionSetup having(DayOfWeek deliveryDay) {
        return new SubscriptionSetup(app, authToken, aSubscription().withDeliveryDay(deliveryDay));
    }

    public SubscriptionSetup having(TestFactory.SubscriptionBuilder subscription) {
        return new SubscriptionSetup(app, authToken, subscription);
    }

}
