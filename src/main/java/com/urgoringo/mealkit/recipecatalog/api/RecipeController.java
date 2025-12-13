package com.urgoringo.mealkit.recipecatalog.api;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import com.urgoringo.mealkit.recipecatalog.application.CreateRecipeService;
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

    private final GetRecipeService getRecipeService;
    private final CreateRecipeService createRecipeService;

    @GetMapping
    public ResponseEntity<List<RecipeResponse>> getAllRecipes() {
        List<GetRecipeService.RecipeWithIngredients> recipes =
                getRecipeService.executeAll();
        
        List<RecipeResponse> response = recipes.stream()
                .map(recipeWithIngredients -> new RecipeResponse(
                        recipeWithIngredients.recipe().id().value(),
                        recipeWithIngredients.recipe().title(),
                        recipeWithIngredients.recipe().instructions(),
                        recipeWithIngredients.ingredientDetails().stream()
                                .map(detail -> new IngredientResponse(
                                        detail.ingredient().name(),
                                        detail.quantity(),
                                        detail.unit()
                                ))
                                .toList()
                ))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getRecipe(@PathVariable Long id) {
        GetRecipeService.RecipeWithIngredients recipeWithIngredients =
                getRecipeService.execute(Id.of(id));
        
        RecipeResponse response = new RecipeResponse(
                recipeWithIngredients.recipe().id().value(),
                recipeWithIngredients.recipe().title(),
                recipeWithIngredients.recipe().instructions(),
                recipeWithIngredients.ingredientDetails().stream()
                        .map(detail -> new IngredientResponse(
                                detail.ingredient().name(),
                                detail.quantity(),
                                detail.unit()
                        ))
                        .toList()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<RecipeResponse> createRecipe(@RequestBody CreateRecipeRequest request) {
        List<CreateRecipeService.IngredientInput> ingredientInputs = request.ingredients().stream()
                .map(ing -> new CreateRecipeService.IngredientInput(ing.name(), ing.quantity(), ing.unit()))
                .toList();
        
        CreateRecipeService.CreateRecipeCommand command = new CreateRecipeService.CreateRecipeCommand(
                request.title(),
                request.instructions(),
                ingredientInputs
        );
        
        Recipe recipe = createRecipeService.execute(command);
        
        GetRecipeService.RecipeWithIngredients recipeWithIngredients =
                getRecipeService.execute(recipe.id());
        
        RecipeResponse response = new RecipeResponse(
                recipeWithIngredients.recipe().id().value(),
                recipeWithIngredients.recipe().title(),
                recipeWithIngredients.recipe().instructions(),
                recipeWithIngredients.ingredientDetails().stream()
                        .map(detail -> new IngredientResponse(
                                detail.ingredient().name(),
                                detail.quantity(),
                                detail.unit()
                        ))
                        .toList()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public record CreateRecipeRequest(
            String title, 
            List<String> instructions,
            List<IngredientRequest> ingredients
    ) {}

    public record IngredientRequest(String name, String quantity, String unit) {}

    public record RecipeResponse(
            Long id, 
            String title, 
            List<String> instructions,
            List<IngredientResponse> ingredients
    ) {}

    public record IngredientResponse(String name, String quantity, String unit) {}
}
