package com.urgoringo.mealkit.recipecatalog.application;

import com.urgoringo.mealkit.domain.Id;
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
import java.util.Map;
import java.util.stream.Collectors;

@NullMarked
@Service
@RequiredArgsConstructor
public class GetRecipeService {

    private final RecipesCatalog recipesCatalog;
    private final IngredientsCatalog ingredientsCatalog;

    public record RecipeWithIngredients(
            Recipe recipe,
            List<IngredientDetail> ingredientDetails
    ) {}

    public record IngredientDetail(
            Ingredient ingredient,
            String quantity,
            String unit
    ) {}

    @Transactional(readOnly = true)
    public RecipeWithIngredients execute(Id<Recipe> recipeId) {
        Recipe recipe = recipesCatalog.findById(recipeId);

        List<IngredientDetail> ingredientDetails = recipe.ingredients().stream()
                .map(ri -> {
                    Ingredient ingredient = ingredientsCatalog.getById(ri.ingredientId());
                    if (ingredient == null) {
                        throw new IllegalStateException("Ingredient not found: " + ri.ingredientId());
                    }
                    return new IngredientDetail(ingredient, ri.quantity(), ri.unit());
                })
                .toList();

        return new RecipeWithIngredients(recipe, ingredientDetails);
    }

    @Transactional(readOnly = true)
    public List<RecipeWithIngredients> executeAll() {
        List<Recipe> recipes = recipesCatalog.findAll();
        List<Ingredient> allIngredients = ingredientsCatalog.findAll();
        
        Map<Id<Ingredient>, Ingredient> ingredientMap = allIngredients.stream()
                .collect(Collectors.toMap(Ingredient::id, ing -> ing));

        return recipes.stream()
                .map(recipe -> {
                    List<IngredientDetail> ingredientDetails = recipe.ingredients().stream()
                            .map(ri -> {
                                Ingredient ingredient = ingredientMap.get(ri.ingredientId());
                                if (ingredient == null) {
                                    throw new IllegalStateException("Ingredient not found: " + ri.ingredientId());
                                }
                                return new IngredientDetail(ingredient, ri.quantity(), ri.unit());
                            })
                            .toList();
                    return new RecipeWithIngredients(recipe, ingredientDetails);
                })
                .toList();
    }
}
