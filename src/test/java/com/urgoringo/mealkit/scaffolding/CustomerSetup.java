package com.urgoringo.mealkit.scaffolding;

import com.urgoringo.mealkit.subscription.api.SubscriptionController;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;

import java.time.DayOfWeek;

import static com.urgoringo.mealkit.scaffolding.TestFactory.aPassword;
import static com.urgoringo.mealkit.scaffolding.TestFactory.anEmail;

@NullMarked
public class CustomerSetup {
    @Getter
    private final String authToken;
    private final ApplicationRunner app;

    public CustomerSetup(ApplicationRunner app) {
        authToken = app.signupCustomer(anEmail(), aPassword()).expectSuccess().token();
        this.app = app;
    }

    public SubscriptionSetup havingSubscription(DayOfWeek dayOfWeek) {
        return new SubscriptionSetup(app, authToken, dayOfWeek);
    }
}
