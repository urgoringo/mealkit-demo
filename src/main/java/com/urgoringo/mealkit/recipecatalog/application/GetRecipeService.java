package com.urgoringo.mealkit.recipecatalog.application;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.recipecatalog.domain.Ingredient;
import com.urgoringo.mealkit.recipecatalog.domain.IngredientsCatalog;
import com.urgoringo.mealkit.recipecatalog.domain.Quantity;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import com.urgoringo.mealkit.recipecatalog.domain.RecipesCatalog;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@NullMarked
@Service
@RequiredArgsConstructor
public class GetRecipeService {

    private final RecipesCatalog recipesCatalog;
    private final IngredientsCatalog ingredientsCatalog;

    public record RecipeWithDetails(
        Recipe recipe,
        List<IngredientDetail> ingredientDetails
    ) {
    }

    public record IngredientDetail(
        Ingredient ingredient,
        Quantity quantity
    ) {
    }

    @Transactional(readOnly = true)
    public RecipeWithDetails execute(Id<Recipe> recipeId) {
        Recipe recipe = recipesCatalog.findById(recipeId);
        return recipeWithDetails(recipe);
    }

    private RecipeWithDetails recipeWithDetails(Recipe recipe) {
        List<IngredientDetail> ingredientDetails = recipe.ingredients().stream()
            .map(ri -> {
                Ingredient ingredient = ingredientsCatalog.getById(ri.ingredientId());
                return new IngredientDetail(ingredient, ri.quantity());
            })
            .toList();

        return new RecipeWithDetails(recipe, ingredientDetails);
    }

    @Transactional(readOnly = true)
    public List<RecipeWithDetails> executeAll() {
        List<Recipe> recipes = recipesCatalog.findAll();
        return recipes.stream()
            .map(this::recipeWithDetails)
            .toList();
    }
}
