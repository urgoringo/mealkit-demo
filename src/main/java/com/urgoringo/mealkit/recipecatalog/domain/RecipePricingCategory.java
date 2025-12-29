package com.urgoringo.mealkit.recipecatalog.domain;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.domain.Money;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record RecipePricingCategory(
        Id<RecipePricingCategory> id,
        PricingCategory name,
        Money price
) {
    public static RecipePricingCategory create(PricingCategory name, Money price) {
        return new RecipePricingCategory(Id.unassigned(), name, price);
    }
}
