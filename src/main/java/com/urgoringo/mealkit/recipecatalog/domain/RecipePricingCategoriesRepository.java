package com.urgoringo.mealkit.recipecatalog.domain;

import com.urgoringo.mealkit.domain.Money;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Map;

import static com.urgoringo.mealkit.jooq.tables.RecipePricingCategories.RECIPE_PRICING_CATEGORIES;
import static java.util.stream.Collectors.toMap;

@Repository
@RequiredArgsConstructor
public class RecipePricingCategoriesRepository {

    private final DSLContext dsl;

    public Money findPriceByCategory(PricingCategory category) {
        var record = dsl.selectFrom(RECIPE_PRICING_CATEGORIES)
                .where(RECIPE_PRICING_CATEGORIES.NAME.eq(category.name()))
                .fetchSingle();
        return Money.of(record.getPrice());
    }

    public Map<PricingCategory, Money> findAll() {
        return dsl.selectFrom(RECIPE_PRICING_CATEGORIES)
                .fetch()
                .stream()
                .collect(toMap(
                    record -> PricingCategory.valueOf(record.getName()),
                    record -> Money.of(record.getPrice())
                ));
    }

}
