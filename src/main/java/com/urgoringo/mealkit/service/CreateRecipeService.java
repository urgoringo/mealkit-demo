package com.urgoringo.mealkit.service;

import com.urgoringo.mealkit.domain.Recipe;
import com.urgoringo.mealkit.domain.RecipeDomainRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for creating a new recipe.
 * Follows DDD principle: one service per use case.
 */
@NullMarked
@Service
@RequiredArgsConstructor
public class CreateRecipeService {

    private final RecipeDomainRepository recipeDomainRepository;

    @Transactional
    public Recipe execute(String title) {
        var recipe = Recipe.create(title);
        return recipeDomainRepository.save(recipe);
    }
}
