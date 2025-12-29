package com.urgoringo.mealkit.recipecatalog.domain;

import com.urgoringo.mealkit.domain.Money;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.urgoringo.mealkit.jooq.tables.RecipePricingCategories.RECIPE_PRICING_CATEGORIES;

@NullMarked
@Repository
@RequiredArgsConstructor
public class RecipePricingCategoriesRepository {

    private final DSLContext dsl;

    public Optional<Money> findPriceByCategory(PricingCategory category) {
        var record = dsl.selectFrom(RECIPE_PRICING_CATEGORIES)
                .where(RECIPE_PRICING_CATEGORIES.NAME.eq(category.name()))
                .fetchOptional();
        return record.map(r -> Money.of(r.getPrice()));
    }

    @Nullable
    public Money findPriceByCategoryOrNull(PricingCategory category) {
        return findPriceByCategory(category).orElse(null);
    }
}
