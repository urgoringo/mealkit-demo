package com.urgoringo.mealkit.recipecatalog.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.urgoringo.mealkit.domain.Id;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.JSON;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

import static com.urgoringo.mealkit.jooq.tables.Recipes.RECIPES;

@NullMarked
@Repository
@RequiredArgsConstructor
public class RecipesCatalog {

    private final DSLContext dsl;
    private final ObjectMapper objectMapper;

    public List<Recipe> findAll() {
        return dsl.selectFrom(RECIPES)
                .fetch()
                .map(record -> new Recipe(
                        Id.of(record.getId()),
                        record.getName(),
                        toList(record.getInstructions()),
                        parseIngredients(record.getIngredientsWithDetails())
                ));
    }

    public Recipe findById(Id<Recipe> id) {
        var record = dsl.selectFrom(RECIPES)
                .where(RECIPES.ID.eq(id.value()))
                .fetchSingle();
        return new Recipe(
                Id.of(record.getId()),
                record.getName(),
                toList(record.getInstructions()),
                parseIngredients(record.getIngredientsWithDetails())
        );
    }

    public Recipe save(Recipe recipe) {
        if (recipe.id().isAssigned()) {
            dsl.update(RECIPES)
                    .set(RECIPES.NAME, recipe.title())
                    .set(RECIPES.INSTRUCTIONS, recipe.instructions().toArray(new String[0]))
                    .set(RECIPES.INGREDIENTS_WITH_DETAILS, toJson(recipe.ingredientsWithDetails()))
                    .where(RECIPES.ID.eq(recipe.id().value()))
                    .execute();
            return recipe;
        } else {
            var record = dsl.insertInto(RECIPES)
                    .set(RECIPES.NAME, recipe.title())
                    .set(RECIPES.INSTRUCTIONS, recipe.instructions().toArray(new String[0]))
                    .set(RECIPES.INGREDIENTS_WITH_DETAILS, toJson(recipe.ingredientsWithDetails()))
                    .returning(RECIPES.ID)
                    .fetchOne();
            if (record == null) {
                throw new IllegalStateException("Failed to insert recipe");
            }
            return new Recipe(
                    Id.of(record.getId()),
                    recipe.title(),
                    recipe.instructions(),
                    recipe.ingredientsWithDetails()
            );
        }
    }

    public void deleteAll() {
        dsl.deleteFrom(RECIPES).execute();
    }

    private List<String> toList(String[] array) {
        return Arrays.asList(array);
    }

    private List<Ingredient> parseIngredients(JSON json) {
        if (json == null) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json.data(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse ingredients JSON", e);
        }
    }

    private JSON toJson(List<Ingredient> ingredients) {
        try {
            return JSON.json(objectMapper.writeValueAsString(ingredients));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize ingredients to JSON", e);
        }
    }
}
