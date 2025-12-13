package com.urgoringo.mealkit.recipecatalog.application;

import com.urgoringo.mealkit.recipecatalog.domain.Ingredient;
import com.urgoringo.mealkit.recipecatalog.domain.IngredientsCatalog;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import com.urgoringo.mealkit.recipecatalog.domain.RecipeIngredient;
import com.urgoringo.mealkit.recipecatalog.domain.RecipesCatalog;
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
    ) {}

    public record IngredientInput(String name, String quantity, String unit) {}

    @Transactional
    public Recipe execute(CreateRecipeCommand command) {
        List<RecipeIngredient> recipeIngredients = command.ingredients().stream()
                .map(input -> {
                    Ingredient ingredient = ingredientsCatalog.findOrCreate(input.name());
                    return RecipeIngredient.create(ingredient.id(), input.quantity(), input.unit());
                })
                .toList();

        var recipe = Recipe.create(command.title(), command.instructions(), recipeIngredients);
        return recipesCatalog.save(recipe);
    }
}
