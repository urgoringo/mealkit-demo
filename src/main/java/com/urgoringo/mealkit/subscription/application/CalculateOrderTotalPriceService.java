package com.urgoringo.mealkit.subscription.application;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.domain.Money;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import com.urgoringo.mealkit.recipecatalog.domain.RecipePrices;
import com.urgoringo.mealkit.recipecatalog.domain.RecipesCatalog;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@NullMarked
@Service
@RequiredArgsConstructor
public class CalculateOrderTotalPriceService {

    private final RecipesCatalog recipesCatalog;
    private final RecipePrices recipePrices;

    @Transactional(readOnly = true)
    public Money execute(List<Id<Recipe>> recipeIds) {
        return recipeIds.stream()
            .map(recipesCatalog::findById)
            .map(Recipe::pricingCategory)
            .map(recipePrices::by)
            .reduce(Money.of(BigDecimal.ZERO), Money::add);
    }
}
