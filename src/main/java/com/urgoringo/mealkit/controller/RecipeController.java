package com.urgoringo.mealkit.controller;

import com.urgoringo.mealkit.domain.Recipe;
import com.urgoringo.mealkit.service.CreateRecipeService;
import com.urgoringo.mealkit.service.GetAllRecipesService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for recipe endpoints.
 */
@NullMarked
@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final GetAllRecipesService getAllRecipesService;
    private final CreateRecipeService createRecipeService;

    @GetMapping
    public ResponseEntity<List<RecipeResponse>> getAllRecipes() {
        List<Recipe> recipes = getAllRecipesService.execute();
        List<RecipeResponse> response = recipes.stream()
                .map(recipe -> new RecipeResponse(recipe.id().value(), recipe.title()))
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<RecipeResponse> createRecipe(@RequestBody CreateRecipeRequest request) {
        Recipe recipe = createRecipeService.execute(request.title());
        RecipeResponse response = new RecipeResponse(recipe.id().value(), recipe.title());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Request DTO for creating a recipe.
     */
    public record CreateRecipeRequest(String title) {}

    /**
     * Response DTO for recipe data.
     */
    public record RecipeResponse(Long id, String title) {}
}
