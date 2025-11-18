package com.urgoringo.mealkit.service;

import com.urgoringo.mealkit.domain.Recipe;
import com.urgoringo.mealkit.domain.RecipeDomainRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service for retrieving all recipes.
 * Follows DDD principle: one service per use case.
 */
@NullMarked
@Service
@RequiredArgsConstructor
public class GetAllRecipesService {

    private final RecipeDomainRepository recipeDomainRepository;

    @Transactional(readOnly = true)
    public List<Recipe> execute() {
        return recipeDomainRepository.findAll();
    }
}
