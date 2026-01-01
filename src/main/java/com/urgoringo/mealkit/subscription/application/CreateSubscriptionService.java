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

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@NullMarked
@Service
@RequiredArgsConstructor
public class CreateSubscriptionService {

    private final Subscriptions subscriptions;
    private final Clock clock;
    private final SubscriptionTaskScheduler taskScheduler;

    @Transactional
    public Subscription execute(Id<Customer> customerId, List<Id<Recipe>> recipeIds, String deliveryAddress, DayOfWeek deliveryDay) {
        LocalDate today = LocalDate.now(clock);
        var subscription = Subscription.signup(customerId, recipeIds, deliveryAddress, deliveryDay, today);
        var savedSubscription = subscriptions.add(subscription);

        taskScheduler.scheduleProcessing(savedSubscription);

        return savedSubscription;
    }
}
