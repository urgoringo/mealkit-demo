package com.urgoringo.mealkit.recipecatalog.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.urgoringo.mealkit.domain.Id;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

import static com.urgoringo.mealkit.jooq.tables.Recipes.RECIPES;

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
                        parseRecipeIngredients(record.getIngredients()),
                        PricingCategory.valueOf(record.getPricingCategory())
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
                parseRecipeIngredients(record.getIngredients()),
                PricingCategory.valueOf(record.getPricingCategory())
        );
    }

    public Recipe add(Recipe recipe) {
        dsl.insertInto(RECIPES)
                .set(RECIPES.ID, recipe.id().value())
                .set(RECIPES.NAME, recipe.title())
                .set(RECIPES.INSTRUCTIONS, recipe.instructions().toArray(new String[0]))
                .set(RECIPES.INGREDIENTS, toJson(recipe.ingredients()))
                .set(RECIPES.PRICING_CATEGORY, recipe.pricingCategory().name())
                .execute();
        return recipe;
    }

    public Recipe update(Recipe recipe) {
        dsl.update(RECIPES)
                .set(RECIPES.NAME, recipe.title())
                .set(RECIPES.INSTRUCTIONS, recipe.instructions().toArray(new String[0]))
                .set(RECIPES.INGREDIENTS, toJson(recipe.ingredients()))
                .set(RECIPES.PRICING_CATEGORY, recipe.pricingCategory().name())
                .where(RECIPES.ID.eq(recipe.id().value()))
                .execute();
        return recipe;
    }

    public void deleteAll() {
        dsl.deleteFrom(RECIPES).execute();
    }

    private List<String> toList(String[] array) {
        return Arrays.asList(array);
    }

    private List<RecipeIngredient> parseRecipeIngredients(JSONB json) {
        if (json == null) {
            return List.of();
        }
        try {
            List<RecipeIngredientDto> dtos = objectMapper.readValue(json.data(), new TypeReference<>() {});
            return dtos.stream()
                    .map(dto -> new RecipeIngredient(
                            Id.of(dto.ingredientId()),
                            Quantity.of(dto.quantity(), Unit.valueOf(dto.unit()))
                    ))
                    .toList();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse recipe ingredients JSON", e);
        }
    }

    private JSONB toJson(List<RecipeIngredient> recipeIngredients) {
        try {
            List<RecipeIngredientDto> dtos = recipeIngredients.stream()
                    .map(ri -> new RecipeIngredientDto(
                            ri.ingredientId().value().toString(),
                            ri.quantity().amount(),
                            ri.quantity().unit().name()
                    ))
                    .toList();
            return JSONB.jsonb(objectMapper.writeValueAsString(dtos));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize recipe ingredients to JSON", e);
        }
    }

    public int count() {
        return dsl.fetchCount(RECIPES);
    }

}
