package com.urgoringo.mealkit.service;

import com.urgoringo.mealkit.domain.Recipe;
import com.urgoringo.mealkit.domain.RecipesCatalog;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@NullMarked
@Service
@RequiredArgsConstructor
public class GetAllRecipesService {

    private final RecipesCatalog recipesCatalog;

    @Transactional(readOnly = true)
    public List<Recipe> execute() {
        return recipesCatalog.findAll();
    }
}
