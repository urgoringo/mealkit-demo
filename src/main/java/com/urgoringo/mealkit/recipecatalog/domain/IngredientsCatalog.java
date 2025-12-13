package com.urgoringo.mealkit.recipecatalog.domain;

import com.urgoringo.mealkit.domain.Id;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.urgoringo.mealkit.jooq.tables.Ingredients.INGREDIENTS;

@NullMarked
@Repository
@RequiredArgsConstructor
public class IngredientsCatalog {

    private final DSLContext dsl;

    public List<Ingredient> findAll() {
        return dsl.selectFrom(INGREDIENTS)
                .fetch()
                .map(record -> new Ingredient(
                        Id.of(record.getId()),
                        record.getName()
                ));
    }

    //TODO should not be needed
    @Nullable
    public Ingredient findByName(String name) {
        var record = dsl.selectFrom(INGREDIENTS)
                .where(INGREDIENTS.NAME.eq(name))
                .fetchOne();
        if (record == null) {
            return null;
        }
        return new Ingredient(
                Id.of(record.getId()),
                record.getName()
        );
    }

    public Ingredient save(Ingredient ingredient) {
        if (ingredient.id().isAssigned()) {
            dsl.update(INGREDIENTS)
                    .set(INGREDIENTS.NAME, ingredient.name())
                    .where(INGREDIENTS.ID.eq(ingredient.id().value()))
                    .execute();
            return ingredient;
        } else {
            var record = dsl.insertInto(INGREDIENTS)
                    .set(INGREDIENTS.NAME, ingredient.name())
                    .returning(INGREDIENTS.ID)
                    .fetchOne();
            if (record == null) {
                throw new IllegalStateException("Failed to insert ingredient");
            }
            return new Ingredient(
                    Id.of(record.getId()),
                    ingredient.name()
            );
        }
    }

    public Ingredient findOrCreate(String name) {
        var existing = findByName(name);
        if (existing != null) {
            return existing;
        }
        return save(Ingredient.create(name));
    }

    public void deleteAll() {
        dsl.deleteFrom(INGREDIENTS).execute();
    }
}
