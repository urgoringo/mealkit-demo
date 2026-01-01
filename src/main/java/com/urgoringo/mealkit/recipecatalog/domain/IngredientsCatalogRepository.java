package com.urgoringo.mealkit.recipecatalog.domain;

import com.urgoringo.mealkit.domain.Id;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.urgoringo.mealkit.jooq.tables.Ingredients.INGREDIENTS;

@NullMarked
@Repository
@RequiredArgsConstructor
public class IngredientsCatalogRepository {

    private final DSLContext dsl;

    public List<Ingredient> findAll() {
        return dsl.selectFrom(INGREDIENTS)
                .fetch()
                .map(record -> new Ingredient(
                        Id.of(record.getId()),
                        record.getName()
                ));
    }

    @Nullable
    public Ingredient findById(Id<Ingredient> id) {
        var record = dsl.selectFrom(INGREDIENTS)
                .where(INGREDIENTS.ID.eq(id.value()))
                .fetchOne();
        if (record == null) {
            return null;
        }
        return new Ingredient(
                Id.of(record.getId()),
                record.getName()
        );
    }

    public Optional<Ingredient> findByName(String name) {
        var record = dsl.selectFrom(INGREDIENTS)
                .where(INGREDIENTS.NAME.eq(name))
                .fetchOptional();
        return record.map(r -> new Ingredient(
                Id.of(r.getId()),
                r.getName()
        ));
    }

    public Ingredient add(Ingredient ingredient) {
        dsl.insertInto(INGREDIENTS)
                .set(INGREDIENTS.ID, ingredient.id().value())
                .set(INGREDIENTS.NAME, ingredient.name())
                .execute();
        return ingredient;
    }

    public Ingredient update(Ingredient ingredient) {
        dsl.update(INGREDIENTS)
                .set(INGREDIENTS.NAME, ingredient.name())
                .where(INGREDIENTS.ID.eq(ingredient.id().value()))
                .execute();
        return ingredient;
    }

    public void deleteAll() {
        dsl.deleteFrom(INGREDIENTS).execute();
    }
}
