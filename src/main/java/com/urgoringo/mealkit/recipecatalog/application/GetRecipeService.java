package com.urgoringo.mealkit.recipecatalog.application;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.domain.Money;
import com.urgoringo.mealkit.recipecatalog.domain.Ingredient;
import com.urgoringo.mealkit.recipecatalog.domain.IngredientsCatalog;
import com.urgoringo.mealkit.recipecatalog.domain.PricingCategory;
import com.urgoringo.mealkit.recipecatalog.domain.Quantity;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import com.urgoringo.mealkit.recipecatalog.domain.RecipesCatalog;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@NullMarked
@Service
@RequiredArgsConstructor
public class GetRecipeService {

    private final RecipesCatalog recipesCatalog;
    private final IngredientsCatalog ingredientsCatalog;
    
    // Price lookup map for pricing categories
    private static final Map<PricingCategory, Money> PRICING = Map.of(
        PricingCategory.LOW, Money.of(5.00),
        PricingCategory.MEDIUM, Money.of(10.50),
        PricingCategory.HIGH, Money.of(15.00)
    );

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

        Money price = PRICING.get(recipe.pricingCategory());
        if (price == null) {
            throw new IllegalStateException("Price not found for pricing category: " + recipe.pricingCategory());
        }

        return new RecipeWithDetails(recipe, ingredientDetails, recipe.pricingCategory(), price);
    }

    @Transactional(readOnly = true)
    public List<RecipeWithDetails> executeAll() {
        List<Recipe> recipes = recipesCatalog.findAll();
        return recipes.stream()
            .map(this::recipeWithDetails)
            .toList();
    }
}
