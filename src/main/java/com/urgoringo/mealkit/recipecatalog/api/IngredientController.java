package com.urgoringo.mealkit.recipecatalog.api;

import com.urgoringo.mealkit.recipecatalog.application.GetIngredientService;
import com.urgoringo.mealkit.recipecatalog.domain.Ingredient;
import com.urgoringo.mealkit.recipecatalog.domain.IngredientsCatalog;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@NullMarked
@RestController
@RequestMapping("/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientsCatalog ingredientsCatalog;
    private final GetIngredientService getIngredientService;

    @PostMapping
    public ResponseEntity<IngredientResponse> createIngredient(@RequestBody CreateIngredientRequest request) {
        Ingredient ingredient = Ingredient.create(request.name);
        Ingredient saved = ingredientsCatalog.save(ingredient);
        return ResponseEntity.ok(new IngredientResponse(saved.id().value(), saved.name()));
    }

    @GetMapping
    public ResponseEntity<IngredientResponse> getIngredientByName(@RequestParam String name) {
        Ingredient ingredient = getIngredientService.execute(name);
        if (ingredient == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new IngredientResponse(ingredient.id().value(), ingredient.name()));
    }

    public record CreateIngredientRequest(String name) {}
    
    public record IngredientResponse(Long id, String name) {}
}
