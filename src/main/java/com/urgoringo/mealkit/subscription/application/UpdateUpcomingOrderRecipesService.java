package com.urgoringo.mealkit.subscription.application;

import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import com.urgoringo.mealkit.subscription.domain.Subscription;
import com.urgoringo.mealkit.subscription.domain.Subscriptions;
import com.urgoringo.mealkit.subscription.domain.PendingOrder;
import com.urgoringo.mealkit.subscription.domain.UpcomingOrder;
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

    @Transactional
    public Subscription execute(Id<Customer> customerId, Id<UpcomingOrder> orderId, List<Id<Recipe>> recipeIds) {
        Subscription subscription = subscriptions.findByCustomerId(customerId);
        Subscription updatedSubscription = subscription.withUpdatedRecipes(orderId, recipeIds);
        return subscriptions.update(updatedSubscription);
    }
}
