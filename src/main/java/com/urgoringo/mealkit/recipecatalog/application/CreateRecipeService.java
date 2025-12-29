package com.urgoringo.mealkit.recipecatalog.application;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.recipecatalog.domain.*;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@NullMarked
@Service
@RequiredArgsConstructor
public class CreateRecipeService {

    private final RecipesCatalog recipesCatalog;
    private final IngredientsCatalog ingredientsCatalog;
    private final RecipePricingCategoriesRepository pricingCategoriesRepository;

    public record CreateRecipeCommand(
        String title,
        List<String> instructions,
        List<IngredientInput> ingredients,
        @Nullable Id<RecipePricingCategory> pricingCategoryId
    ) {
    }

    public record IngredientInput(Id<Ingredient> ingredientId, Quantity quantity) {
    }

    @Transactional
    public Recipe execute(CreateRecipeCommand command) {
        List<RecipeIngredient> recipeIngredients = command.ingredients().stream()
            .map(input -> RecipeIngredient.create(input.ingredientId(), input.quantity()))
            .toList();

        Id<RecipePricingCategory> pricingCategoryId = command.pricingCategoryId();
        if (pricingCategoryId == null) {
            RecipePricingCategory mediumCategory = pricingCategoriesRepository.findByName(PricingCategory.MEDIUM)
                .orElseThrow(() -> new IllegalStateException("MEDIUM pricing category not found"));
            pricingCategoryId = mediumCategory.id();
        }

        var recipe = Recipe.create(command.title(), command.instructions(), recipeIngredients, pricingCategoryId);
        return recipesCatalog.save(recipe);
    }
}
