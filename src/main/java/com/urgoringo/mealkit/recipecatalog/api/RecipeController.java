package com.urgoringo.mealkit.recipecatalog.api;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.recipecatalog.domain.Quantity;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import com.urgoringo.mealkit.recipecatalog.domain.Unit;
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

import static com.urgoringo.mealkit.recipecatalog.application.CreateRecipeService.*;
import static com.urgoringo.mealkit.recipecatalog.application.CreateRecipeService.IngredientInput;

@NullMarked
@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final GetRecipeService getRecipeService;
    private final CreateRecipeService createRecipeService;

    @GetMapping
    public ResponseEntity<List<RecipeResponse>> getAllRecipes() {
        List<GetRecipeService.RecipeWithDetails> recipes =
            getRecipeService.executeAll();

        List<RecipeResponse> response = recipes.stream()
            .map(recipeWithDetails -> new RecipeResponse(
                recipeWithDetails.recipe().id().value(),
                recipeWithDetails.recipe().title(),
                recipeWithDetails.recipe().instructions(),
                recipeWithDetails.ingredientDetails().stream()
                    .map(detail -> new IngredientResponse(
                        detail.ingredient().name(),
                        detail.quantity().amount(),
                        detail.quantity().unit()
                    ))
                    .toList()
            ))
            .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getRecipe(@PathVariable Long id) {
        GetRecipeService.RecipeWithDetails recipeWithDetails =
            getRecipeService.execute(Id.of(id));

        RecipeResponse response = new RecipeResponse(
            recipeWithDetails.recipe().id().value(),
            recipeWithDetails.recipe().title(),
            recipeWithDetails.recipe().instructions(),
            recipeWithDetails.ingredientDetails().stream()
                .map(detail -> new IngredientResponse(
                    detail.ingredient().name(),
                    detail.quantity().amount(),
                    detail.quantity().unit()
                ))
                .toList()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<RecipeResponse> createRecipe(@RequestBody CreateRecipeRequest request) {
        List<IngredientInput> ingredientInputs = request.ingredients().stream()
            .map(ing -> new IngredientInput(ing.name(), new Quantity(ing.amount(), ing.unit())))
            .toList();

        CreateRecipeCommand command = new CreateRecipeCommand(
            request.title(),
            request.instructions(),
            ingredientInputs
        );

        Recipe recipe = createRecipeService.execute(command);

        GetRecipeService.RecipeWithDetails recipeWithDetails =
            getRecipeService.execute(recipe.id());

        RecipeResponse response = new RecipeResponse(
            recipeWithDetails.recipe().id().value(),
            recipeWithDetails.recipe().title(),
            recipeWithDetails.recipe().instructions(),
            recipeWithDetails.ingredientDetails().stream()
                .map(detail -> new IngredientResponse(
                    detail.ingredient().name(),
                    detail.quantity().amount(),
                    detail.quantity().unit()
                ))
                .toList()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public record CreateRecipeRequest(
        String title,
        List<String> instructions,
        List<IngredientRequest> ingredients
    ) {
    }

    public record IngredientRequest(String name, String amount, Unit unit) {
    }

    public record RecipeResponse(
        Long id,
        String title,
        List<String> instructions,
        List<IngredientResponse> ingredients
    ) {
    }

    public record IngredientResponse(String name, String quantity, Unit unit) {
    }
}
