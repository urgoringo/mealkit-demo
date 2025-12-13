package com.urgoringo.mealkit.recipecatalog.application;

import com.urgoringo.mealkit.recipecatalog.domain.Ingredient;
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
public class CreateRecipeService {

    private final RecipesCatalog recipesCatalog;

    @Transactional
    public Recipe execute(String title, List<String> instructions, List<Ingredient> ingredientsWithDetails) {
        var recipe = Recipe.create(title, instructions, ingredientsWithDetails);
        return recipesCatalog.save(recipe);
    }
}
