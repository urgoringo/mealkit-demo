package com.urgoringo.mealkit.domain;

import com.urgoringo.mealkit.mapper.RecipeMapper;
import com.urgoringo.mealkit.persistence.RecipeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Repository;

import java.util.List;

@NullMarked
@Repository
@RequiredArgsConstructor
public class RecipesCatalog {

    private final RecipeJpaRepository recipeJpaRepository;
    private final RecipeMapper recipeMapper;

    public List<Recipe> findAll() {
        return recipeMapper.toDomain(recipeJpaRepository.findAll());
    }

    public Recipe save(Recipe recipe) {
        var entity = recipeMapper.toEntity(recipe);
        var savedEntity = recipeJpaRepository.save(entity);
        return recipeMapper.toDomain(savedEntity);
    }
}
