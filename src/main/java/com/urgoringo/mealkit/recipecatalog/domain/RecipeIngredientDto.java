package com.urgoringo.mealkit.recipecatalog.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

record RecipeIngredientDto(
        @JsonProperty("ingredient_id") String ingredientId,
        String quantity,
        String unit
) {}
