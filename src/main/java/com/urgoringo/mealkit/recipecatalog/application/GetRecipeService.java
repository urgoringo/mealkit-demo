package com.urgoringo.mealkit.recipecatalog.application;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import com.urgoringo.mealkit.recipecatalog.domain.RecipesCatalog;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@NullMarked
@Service
@RequiredArgsConstructor
public class GetRecipeService {

    private final RecipesCatalog recipesCatalog;

    @Transactional(readOnly = true)
    public @Nullable Recipe execute(Id<Recipe> id) {
        return recipesCatalog.findById(id);
    }
}
