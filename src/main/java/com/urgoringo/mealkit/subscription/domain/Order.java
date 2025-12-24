package com.urgoringo.mealkit.subscription.domain;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;

import java.time.LocalDate;
import java.util.List;

public sealed interface Order permits UpcomingOrder, DeliveredOrder {
    Id<Order> id();
    List<Id<Recipe>> recipeIds();
    LocalDate deliveryDate();
    OrderStatus status();
}
