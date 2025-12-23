package com.urgoringo.mealkit.subscription.application;

import com.urgoringo.mealkit.domain.*;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import com.urgoringo.mealkit.recipecatalog.domain.RecipesCatalog;
import com.urgoringo.mealkit.subscription.domain.Subscription;
import com.urgoringo.mealkit.subscription.domain.Subscriptions;
import com.urgoringo.mealkit.subscription.domain.UpcomingOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
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
public class SelectRecipesForUpcomingOrdersService {

    private final Subscriptions subscriptions;
    private final RecipesCatalog recipesCatalog;
    private final Clock clock;
    private final TransactionTemplate transactionTemplate;

    private static final Duration PRESELECTION_THRESHOLD_DAYS = ofDays(3);

    @Scheduled(cron = "0 0 2 * * *") // Daily at 2 AM UTC
    public void execute() {
        log.info("Starting scheduled job to process subscription orders");
        LocalDate currentDate = LocalDate.now(clock);
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
        List<Id<UpcomingOrder>> ordersToLock = subscription.ordersToLock(clock);
        ordersToLock.forEach(subscriptions::lockOrder);

        List<Recipe> allRecipes = recipesCatalog.findAll();
        Collections.shuffle(allRecipes);
        List<Id<Recipe>> selectedRecipeIds = allRecipes.stream()
            .limit(3)
            .map(Recipe::id)
            .toList();

        LocalDate currentDate = LocalDate.now(clock);
        Subscription updatedSubscription = subscription.withNewUpcomingOrder(currentDate, selectedRecipeIds);

        if (updatedSubscription != subscription) {
            subscriptions.save(updatedSubscription);
        }
    }
}
