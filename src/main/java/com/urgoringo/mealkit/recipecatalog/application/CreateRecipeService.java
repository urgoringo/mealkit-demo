package com.urgoringo.mealkit.recipecatalog.application;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.recipecatalog.domain.*;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateRecipeService {

    private final RecipesCatalog recipesCatalog;
    private final IngredientsCatalog ingredientsCatalog;

    public record CreateRecipeCommand(
        String title,
        List<String> instructions,
        List<IngredientInput> ingredients,
        @Nullable PricingCategory pricingCategory
    ) {
    }

    public record IngredientInput(Id<Ingredient> ingredientId, Quantity quantity) {
    }

    @Transactional
    public Recipe execute(CreateRecipeCommand command) {
        List<RecipeIngredient> recipeIngredients = command.ingredients().stream()
            .map(input -> RecipeIngredient.create(input.ingredientId(), input.quantity()))
            .toList();

        PricingCategory pricingCategory = command.pricingCategory() != null 
            ? command.pricingCategory() 
            : PricingCategory.MEDIUM;

        var recipe = Recipe.create(command.title(), command.instructions(), recipeIngredients, pricingCategory);
        return recipesCatalog.add(recipe);
    }
}
