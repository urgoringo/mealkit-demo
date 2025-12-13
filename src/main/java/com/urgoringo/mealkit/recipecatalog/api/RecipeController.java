package com.urgoringo.mealkit.recipecatalog.api;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import com.urgoringo.mealkit.recipecatalog.application.CreateRecipeService;
import com.urgoringo.mealkit.recipecatalog.application.GetAllRecipesService;
import com.urgoringo.mealkit.recipecatalog.application.GetRecipeService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@NullMarked
@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final GetAllRecipesService getAllRecipesService;
    private final GetRecipeService getRecipeService;
    private final CreateRecipeService createRecipeService;

    @GetMapping
    public ResponseEntity<List<RecipeResponse>> getAllRecipes() {
        List<Recipe> recipes = getAllRecipesService.execute();
        List<RecipeResponse> response = recipes.stream()
                .map(recipe -> new RecipeResponse(
                        recipe.id().value(),
                        recipe.title(),
                        recipe.ingredients(),
                        recipe.instructions()
                ))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getRecipe(@PathVariable Long id) {
        Recipe recipe = getRecipeService.execute(Id.of(id));
        if (recipe == null) {
            return ResponseEntity.notFound().build();
        }
        RecipeResponse response = new RecipeResponse(
                recipe.id().value(),
                recipe.title(),
                recipe.ingredients(),
                recipe.instructions()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<RecipeResponse> createRecipe(@RequestBody CreateRecipeRequest request) {
        Recipe recipe = createRecipeService.execute(
                request.title(),
                request.ingredients(),
                request.instructions()
        );
        RecipeResponse response = new RecipeResponse(
                recipe.id().value(),
                recipe.title(),
                recipe.ingredients(),
                recipe.instructions()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public record CreateRecipeRequest(String title, List<String> ingredients, List<String> instructions) {}

    public record RecipeResponse(Long id, String title, List<String> ingredients, List<String> instructions) {}
}
