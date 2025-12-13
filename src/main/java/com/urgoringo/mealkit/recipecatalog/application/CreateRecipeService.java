package com.urgoringo.mealkit.recipecatalog.application;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.recipecatalog.domain.*;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@NullMarked
@Service
@RequiredArgsConstructor
public class CreateRecipeService {

    private final RecipesCatalog recipesCatalog;
    private final IngredientsCatalog ingredientsCatalog;

    public record CreateRecipeCommand(
        String title,
        List<String> instructions,
        List<IngredientInput> ingredients
    ) {
    }

    public record IngredientInput(Id<Ingredient> ingredientId, Quantity quantity) {
    }

    @Transactional
    public Recipe execute(CreateRecipeCommand command) {
        List<RecipeIngredient> recipeIngredients = command.ingredients().stream()
            .map(input -> RecipeIngredient.create(input.ingredientId(), input.quantity()))
            .toList();

        var recipe = Recipe.create(command.title(), command.instructions(), recipeIngredients);
        return recipesCatalog.save(recipe);
    }
}
