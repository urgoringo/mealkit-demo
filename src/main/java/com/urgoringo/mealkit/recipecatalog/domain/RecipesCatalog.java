package com.urgoringo.mealkit.recipecatalog.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
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
                        parseRecipeIngredients(record.getIngredients())
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
                parseRecipeIngredients(record.getIngredients())
        );
    }

    public Recipe save(Recipe recipe) {
        if (recipe.id().isAssigned()) {
            dsl.update(RECIPES)
                    .set(RECIPES.NAME, recipe.title())
                    .set(RECIPES.INSTRUCTIONS, recipe.instructions().toArray(new String[0]))
                    .set(RECIPES.INGREDIENTS, toJson(recipe.ingredients()))
                    .where(RECIPES.ID.eq(recipe.id().value()))
                    .execute();
            return recipe;
        } else {
            var record = dsl.insertInto(RECIPES)
                    .set(RECIPES.NAME, recipe.title())
                    .set(RECIPES.INSTRUCTIONS, recipe.instructions().toArray(new String[0]))
                    .set(RECIPES.INGREDIENTS, toJson(recipe.ingredients()))
                    .returning(RECIPES.ID)
                    .fetchOne();
            if (record == null) {
                throw new IllegalStateException("Failed to insert recipe");
            }
            return new Recipe(
                    Id.of(record.getId()),
                    recipe.title(),
                    recipe.instructions(),
                    recipe.ingredients()
            );
        }
    }

    public void deleteAll() {
        dsl.deleteFrom(RECIPES).execute();
    }

    private List<String> toList(String[] array) {
        return Arrays.asList(array);
    }

    private List<RecipeIngredient> parseRecipeIngredients(JSON json) {
        if (json == null) {
            return List.of();
        }
        try {
            List<RecipeIngredientDto> dtos = objectMapper.readValue(json.data(), new TypeReference<>() {});
            return dtos.stream()
                    .map(dto -> new RecipeIngredient(
                            Id.of(dto.ingredientId()),
                            Quantity.of(dto.quantity(), dto.unit())
                    ))
                    .toList();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse recipe ingredients JSON", e);
        }
    }

    private JSON toJson(List<RecipeIngredient> recipeIngredients) {
        try {
            List<RecipeIngredientDto> dtos = recipeIngredients.stream()
                    .map(ri -> new RecipeIngredientDto(
                            ri.ingredientId().value(),
                            ri.quantity().amount(),
                            ri.quantity().unit()
                    ))
                    .toList();
            return JSON.json(objectMapper.writeValueAsString(dtos));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize recipe ingredients to JSON", e);
        }
    }

    private record RecipeIngredientDto(
            @JsonProperty("ingredient_id") Long ingredientId,
            String quantity,
            String unit
    ) {}
}
