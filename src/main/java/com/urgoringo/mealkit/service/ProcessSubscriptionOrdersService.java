package com.urgoringo.mealkit.service;

import com.urgoringo.mealkit.domain.*;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@NullMarked
@Service
@RequiredArgsConstructor
public class ProcessSubscriptionOrdersService {

    private final Subscriptions subscriptions;
    private final RecipesCatalog recipesCatalog;
    private final Clock clock;

    @Transactional
    public void execute(Id<Subscription> subscriptionId) {
        Subscription subscription = subscriptions.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found: " + subscriptionId.value()));

        List<Recipe> allRecipes = recipesCatalog.findAll();
        if (allRecipes.size() < 3) {
            throw new IllegalStateException("Not enough recipes available to create an order. Need at least 3, found: " + allRecipes.size());
        }

        Collections.shuffle(allRecipes);
        List<Id<Recipe>> selectedRecipeIds = allRecipes.stream()
                .limit(3)
                .map(Recipe::id)
                .toList();

        LocalDate currentDate = LocalDate.now(clock);
        Subscription updatedSubscription = subscription.processOrders(currentDate, selectedRecipeIds);

        if (updatedSubscription != subscription) {
            subscriptions.save(updatedSubscription);
        }
    }
}
