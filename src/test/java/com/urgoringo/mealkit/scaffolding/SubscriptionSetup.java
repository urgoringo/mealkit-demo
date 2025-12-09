package com.urgoringo.mealkit.scaffolding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import static com.urgoringo.mealkit.scaffolding.ApplicationRunner.SubscriptionRequest.aSubscription;
import static com.urgoringo.mealkit.scaffolding.TestFactory.aPassword;
import static com.urgoringo.mealkit.scaffolding.TestFactory.anEmail;
import static java.time.DayOfWeek.WEDNESDAY;

@Component
@RequiredArgsConstructor
public class ApplicationSetup {
    private final ApplicationRunner app;
    private List<Long> recipeIds;
    @Getter
    private String authToken;
    private ApplicationRunner.SubscriptionResponse subscription;

    public ApplicationSetup havingRecipes(int countOfRecipes) {
        recipeIds = IntStream.rangeClosed(1, countOfRecipes)
                .mapToObj(i -> app.havingRecipe("Recipe " + i))
                .map(ApplicationRunner.RecipeResponse::id)
                .toList();
        return this;
    }

    public ApplicationSetup havingCustomer() {
        authToken = app.signupCustomer(anEmail(), aPassword()).expectSuccess().token();
        return this;
    }

    public ApplicationSetup havingSubscription(DayOfWeek deliveryDay) {
        if (authToken == null) {
            havingCustomer();
        }
        if (recipeIds == null) {
            havingRecipes(3);
        }

        subscription = app.create(
                authToken,
                aSubscription(recipeIds).withDeliveryDay(deliveryDay)
        ).expectSuccess();
        return this;
    }

    public ApplicationSetup havingSubscription() {
        havingSubscription(WEDNESDAY);
        return this;
    }

    public ApplicationRunner.SubscriptionResponse getCustomerSubscription() {
        if (subscription == null) {
            havingSubscription();
        }
        return app.getCustomerSubscription(authToken).expectSuccess();
    }

    public ApplicationSetup freezeTimeOn(LocalDate localDate) {
        app.freezeTimeOn(localDate);
        return this;
    }
}
