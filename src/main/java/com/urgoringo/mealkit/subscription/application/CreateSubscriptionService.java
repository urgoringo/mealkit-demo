package com.urgoringo.mealkit.subscription.application;

import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.customer.domain.Customers;
import com.urgoringo.mealkit.domain.*;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import com.urgoringo.mealkit.auth.PasswordHasher;
import com.urgoringo.mealkit.subscription.domain.Subscription;
import com.urgoringo.mealkit.subscription.domain.Subscriptions;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@NullMarked
@Service
@RequiredArgsConstructor
public class CreateSubscriptionService {

    private final Subscriptions subscriptions;
    private final Clock clock;

    @Transactional
    public Subscription execute(Id<Customer> customerId, List<Id<Recipe>> recipeIds, String deliveryAddress, DayOfWeek deliveryDay) {
        LocalDate today = LocalDate.now(clock);
        var subscription = Subscription.signup(customerId, recipeIds, deliveryAddress, deliveryDay, today);

        return subscriptions.save(subscription);
    }
}
