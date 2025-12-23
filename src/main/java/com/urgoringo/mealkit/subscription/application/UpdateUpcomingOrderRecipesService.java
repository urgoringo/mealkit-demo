package com.urgoringo.mealkit.subscription.application;

import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.domain.ValidationException;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import com.urgoringo.mealkit.subscription.domain.OrderStatus;
import com.urgoringo.mealkit.subscription.domain.Subscription;
import com.urgoringo.mealkit.subscription.domain.Subscriptions;
import com.urgoringo.mealkit.subscription.domain.UpcomingOrder;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;

@NullMarked
@Service
@RequiredArgsConstructor
public class UpdateUpcomingOrderRecipesService {

    private final Subscriptions subscriptions;
    private final Clock clock;

    @Transactional
    public Subscription execute(Id<Customer> customerId, Id<UpcomingOrder> orderId, List<Id<Recipe>> recipeIds) {
        Subscription subscription = subscriptions.findByCustomerId(customerId);
        
        UpcomingOrder orderToUpdate = subscription.upcomingOrders().stream()
                .filter(order -> order.id().equals(orderId))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Order not found"));
        
        if (orderToUpdate.status(clock) == OrderStatus.LOCKED) {
            throw new ValidationException("Cannot update locked order");
        }
        
        Subscription updatedSubscription = subscription.withUpdatedRecipes(orderId, recipeIds);
        return subscriptions.save(updatedSubscription);
    }
}
