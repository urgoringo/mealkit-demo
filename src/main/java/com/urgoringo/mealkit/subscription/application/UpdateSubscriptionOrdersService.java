package com.urgoringo.mealkit.subscription.application;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import com.urgoringo.mealkit.recipecatalog.domain.RecipesCatalog;
import com.urgoringo.mealkit.subscription.domain.Subscription;
import com.urgoringo.mealkit.subscription.domain.Subscriptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.moments.DayHasPassed;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static java.time.Duration.ofDays;

@NullMarked
@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateSubscriptionOrdersService {

    private final Subscriptions subscriptions;
    private final RecipesCatalog recipesCatalog;
    private final Clock clock;
    private final TransactionTemplate transactionTemplate;

    private static final Duration PRESELECTION_THRESHOLD_DAYS = ofDays(3);

    @TransactionalEventListener
    public void on(DayHasPassed event) {
        log.info("Starting scheduled job to process subscription orders");
        LocalDate currentDate = event.getDate();
        LocalDate thresholdDate = currentDate.plusDays(PRESELECTION_THRESHOLD_DAYS.toDays());
        List<Id<Subscription>> subscriptionIds = subscriptions.findSubscriptionsWithPendingOrdersByDeliveryDate(thresholdDate);
        log.info("Found {} subscriptions requiring recipe preselection", subscriptionIds.size());

        for (Id<Subscription> subscriptionId : subscriptionIds) {
            try {
                transactionTemplate.executeWithoutResult(_ -> {
                    Subscription subscription = subscriptions.findById(subscriptionId)
                        .orElseThrow(() -> new IllegalArgumentException("Subscription not found: " + subscriptionId.value()));
                    processSubscription(subscription);
                });
            } catch (Exception e) {
                log.error("Error processing subscription {}: {}", subscriptionId.value(), e.getMessage(), e);
            }
        }
        log.info("Completed scheduled job to process subscription orders");
    }

    private void processSubscription(Subscription subscription) {
        List<Recipe> allRecipes = recipesCatalog.findAll();
        Collections.shuffle(allRecipes);
        List<Id<Recipe>> selectedRecipeIds = allRecipes.stream()
            .limit(3)
            .map(Recipe::id)
            .toList();

        Subscription updatedSubscription = subscription
            .withNewUpcomingOrder(selectedRecipeIds)
            .withLockedUpcomingOrder(clock);

        subscriptions.save(updatedSubscription);
    }
}
