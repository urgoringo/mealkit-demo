package com.urgoringo.mealkit.subscription.domain;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import org.jspecify.annotations.NullMarked;

import java.time.LocalDate;
import java.util.List;

@NullMarked
public record DeliveredOrder(
    Id<DeliveredOrder> id,
    List<Id<Recipe>> recipeIds,
    LocalDate deliveryDate
) {
}
