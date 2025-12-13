package com.urgoringo.mealkit.recipecatalog.application;

import com.urgoringo.mealkit.recipecatalog.domain.Ingredient;
import com.urgoringo.mealkit.recipecatalog.domain.IngredientsCatalog;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@NullMarked
@Service
@RequiredArgsConstructor
public class GetIngredientService {

    private final IngredientsCatalog ingredientsCatalog;

    @Nullable
    public Ingredient execute(String name) {
        return ingredientsCatalog.findByName(name);
    }
}
