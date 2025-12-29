package com.urgoringo.mealkit.recipecatalog.application;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.domain.Money;
import com.urgoringo.mealkit.recipecatalog.domain.Ingredient;
import com.urgoringo.mealkit.recipecatalog.domain.IngredientsCatalog;
import com.urgoringo.mealkit.recipecatalog.domain.PricingCategory;
import com.urgoringo.mealkit.recipecatalog.domain.Quantity;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import com.urgoringo.mealkit.recipecatalog.domain.RecipesCatalog;
import com.urgoringo.mealkit.recipecatalog.domain.RecipePricingCategoriesRepository;
import com.urgoringo.mealkit.recipecatalog.domain.RecipePricingCategory;
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
    private final RecipePricingCategoriesRepository pricingCategoriesRepository;

    public record RecipeWithDetails(
        Recipe recipe,
        List<IngredientDetail> ingredientDetails,
        PricingCategory pricingCategory,
        Money price
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

        RecipePricingCategory recipePricingCategory = pricingCategoriesRepository.findById(recipe.pricingCategoryId());
        if (recipePricingCategory == null) {
            throw new IllegalStateException("Recipe pricing category not found: " + recipe.pricingCategoryId());
        }

        return new RecipeWithDetails(recipe, ingredientDetails, recipePricingCategory.name(), recipePricingCategory.price());
    }

    @Transactional(readOnly = true)
    public List<RecipeWithDetails> executeAll() {
        List<Recipe> recipes = recipesCatalog.findAll();
        return recipes.stream()
            .map(this::recipeWithDetails)
            .toList();
    }
}
