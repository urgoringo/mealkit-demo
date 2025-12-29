package com.urgoringo.mealkit.recipecatalog.domain;

import com.urgoringo.mealkit.domain.Id;
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

    public Optional<RecipePricingCategory> findByName(PricingCategory name) {
        var record = dsl.selectFrom(RECIPE_PRICING_CATEGORIES)
                .where(RECIPE_PRICING_CATEGORIES.NAME.eq(name.name()))
                .fetchOptional();
        return record.map(r -> new RecipePricingCategory(
                Id.of(r.getId()),
                PricingCategory.valueOf(r.getName()),
                Money.of(r.getPrice())
        ));
    }

    @Nullable
    public RecipePricingCategory findById(Id<RecipePricingCategory> id) {
        var record = dsl.selectFrom(RECIPE_PRICING_CATEGORIES)
                .where(RECIPE_PRICING_CATEGORIES.ID.eq(id.value()))
                .fetchOne();
        if (record == null) {
            return null;
        }
        return new RecipePricingCategory(
                Id.of(record.getId()),
                PricingCategory.valueOf(record.getName()),
                Money.of(record.getPrice())
        );
    }

    public RecipePricingCategory save(RecipePricingCategory category) {
        if (category.id().isAssigned()) {
            dsl.update(RECIPE_PRICING_CATEGORIES)
                    .set(RECIPE_PRICING_CATEGORIES.NAME, category.name().name())
                    .set(RECIPE_PRICING_CATEGORIES.PRICE, category.price().amount())
                    .where(RECIPE_PRICING_CATEGORIES.ID.eq(category.id().value()))
                    .execute();
            return category;
        } else {
            var record = dsl.insertInto(RECIPE_PRICING_CATEGORIES)
                    .set(RECIPE_PRICING_CATEGORIES.NAME, category.name().name())
                    .set(RECIPE_PRICING_CATEGORIES.PRICE, category.price().amount())
                    .returning(RECIPE_PRICING_CATEGORIES.ID)
                    .fetchOne();
            if (record == null) {
                throw new IllegalStateException("Failed to insert recipe pricing category");
            }
            return new RecipePricingCategory(
                    Id.of(record.getId()),
                    category.name(),
                    category.price()
            );
        }
    }

    public void deleteAll() {
        dsl.deleteFrom(RECIPE_PRICING_CATEGORIES).execute();
    }
}
