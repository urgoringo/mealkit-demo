package com.urgoringo.mealkit.domain;

import com.urgoringo.mealkit.mapper.RecipeMapper;
import com.urgoringo.mealkit.persistence.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Domain repository for Recipe.
 * Transforms persistence entities to domain models using RecipeMapper.
 */
@NullMarked
@Repository
@RequiredArgsConstructor
public class RecipeDomainRepository {

    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;

    public List<Recipe> findAll() {
        return recipeMapper.toDomain(recipeRepository.findAll());
    }

    public Recipe save(Recipe recipe) {
        var entity = recipeMapper.toEntity(recipe);
        var savedEntity = recipeRepository.save(entity);
        return recipeMapper.toDomain(savedEntity);
    }
}
