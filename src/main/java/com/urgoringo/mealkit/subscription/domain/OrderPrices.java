package com.urgoringo.mealkit.subscription.domain;

import com.urgoringo.mealkit.domain.Money;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import com.urgoringo.mealkit.recipecatalog.domain.RecipePrices;
import com.urgoringo.mealkit.recipecatalog.domain.RecipesCatalog;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;

@NullMarked
@Component
@RequiredArgsConstructor
public class OrderPrices {

    private final RecipesCatalog recipesCatalog;
    private final RecipePrices recipePrices;

    public Money totalPrice(Order order) {
        return order.recipeIds().stream()
            .map(recipesCatalog::findById)
            .map(Recipe::pricingCategory)
            .map(recipePrices::by)
            .reduce(Money.ZERO, Money::add);
    }
}
