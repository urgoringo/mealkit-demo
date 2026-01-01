package com.urgoringo.mealkit.subscription.application;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import com.urgoringo.mealkit.recipecatalog.domain.RecipesCatalog;
import com.urgoringo.mealkit.subscription.domain.Subscription;
import com.urgoringo.mealkit.subscription.domain.SubscriptionProcessedEvent;
import com.urgoringo.mealkit.subscription.domain.Subscriptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Collections;
import java.util.List;

@NullMarked
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessSubscriptionOrdersService {

    private final Subscriptions subscriptions;
    private final RecipesCatalog recipesCatalog;
    private final Clock clock;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void execute(String subscriptionIdValue) {
        log.info("Processing subscription {}", subscriptionIdValue);
        Id<Subscription> subscriptionId = Id.of(subscriptionIdValue);
        Subscription subscription = subscriptions.findById(subscriptionId);

        List<Recipe> allRecipes = recipesCatalog.findAll();

        Collections.shuffle(allRecipes);
        List<Id<Recipe>> selectedRecipeIds = allRecipes.stream()
            .limit(3)
            .map(Recipe::id)
            .toList();

        Subscription updatedSubscription = subscription
            .withNewUpcomingOrder(selectedRecipeIds)
            .withLockedUpcomingOrder(clock);

        subscriptions.update(updatedSubscription);

        applicationEventPublisher.publishEvent(new SubscriptionProcessedEvent(updatedSubscription));
    }
}
