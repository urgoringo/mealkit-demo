package com.urgoringo.mealkit.subscription.application;

import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import com.urgoringo.mealkit.subscription.domain.Subscription;
import com.urgoringo.mealkit.subscription.domain.Subscriptions;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@NullMarked
@Service
@RequiredArgsConstructor
public class UpdateUpcomingOrderRecipesService {

    private final Subscriptions subscriptions;
    private final GetSubscriptionService getSubscriptionService;

    @Transactional
    public Subscription execute(Id<Customer> customerId, List<Id<Recipe>> recipeIds) {
        Subscription subscription = getSubscriptionService.executeForAuthenticatedCustomer(customerId);
        Subscription updatedSubscription = subscription.withUpdatedRecipesForFirstUpcomingOrder(recipeIds);
        return subscriptions.save(updatedSubscription);
    }
}
