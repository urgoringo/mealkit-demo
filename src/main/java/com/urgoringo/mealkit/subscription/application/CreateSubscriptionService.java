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

    private final Customers customers;
    private final Subscriptions subscriptions;
    private final PasswordHasher passwordHasher;
    private final Clock clock;

    @Transactional
    public Subscription execute(String customerEmail, List<Id<Recipe>> recipeIds, String deliveryAddress, @Nullable DayOfWeek deliveryDay) {
        Customer customer;
        var existingCustomer = customers.findByEmail(customerEmail);

        if (existingCustomer.isPresent()) {
            customer = existingCustomer.get();
        } else {
            String temporaryPassword = "TEMP_" + UUID.randomUUID();
            String hashedPassword = passwordHasher.hash(temporaryPassword);
            customer = Customer.signup(customerEmail, hashedPassword);
            customer = customers.save(customer);
        }

        LocalDate today = LocalDate.now(clock);
        var subscription = Subscription.signup(customer.id(), recipeIds, deliveryAddress, deliveryDay, today);

        return subscriptions.save(subscription);
    }
}
