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

/**
 * Application service for processing subscription orders.
 * Checks if new orders should be added to the subscription based on the current date.
 * Automatically selects recipes randomly from available recipes.
 * Follows DDD principle: one service per use case.
 */
@NullMarked
@Service
@RequiredArgsConstructor
public class ProcessSubscriptionOrdersService {

    private final SubscriptionDomainRepository subscriptionDomainRepository;
    private final RecipeDomainRepository recipeDomainRepository;
    private final Clock clock;

    @Transactional
    public void execute(Id<Subscription> subscriptionId) {
        Subscription subscription = subscriptionDomainRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found: " + subscriptionId.value()));

        // Fetch all available recipes
        List<Recipe> allRecipes = recipeDomainRepository.findAll();
        if (allRecipes.size() < 3) {
            throw new IllegalStateException("Not enough recipes available to create an order. Need at least 3, found: " + allRecipes.size());
        }

        // Randomly select 3 recipes
        Collections.shuffle(allRecipes);
        List<Id<Recipe>> selectedRecipeIds = allRecipes.stream()
                .limit(3)
                .map(Recipe::id)
                .toList();

        LocalDate currentDate = LocalDate.now(clock);
        Subscription updatedSubscription = subscription.processOrders(currentDate, selectedRecipeIds);

        if (updatedSubscription != subscription) {
            subscriptionDomainRepository.save(updatedSubscription);
        }
    }
}
