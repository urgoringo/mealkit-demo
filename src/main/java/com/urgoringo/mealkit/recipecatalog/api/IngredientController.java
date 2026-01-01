package com.urgoringo.mealkit.recipecatalog.api;

import com.urgoringo.mealkit.recipecatalog.application.GetIngredientService;
import com.urgoringo.mealkit.recipecatalog.domain.Ingredient;
import com.urgoringo.mealkit.recipecatalog.domain.IngredientsCatalog;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.*;

@NullMarked
@RestController
@RequestMapping("/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientsCatalog ingredientsCatalog;
    private final GetIngredientService getIngredientService;

    @PostMapping
    public ResponseEntity<IngredientResponse> createIngredient(@RequestBody CreateIngredientRequest request) {
        Ingredient saved = ingredientsCatalog.findOrAdd(Ingredient.create(request.name));
        return ok(new IngredientResponse(saved.id().value().toString(), saved.name()));
    }

    @GetMapping
    public ResponseEntity<IngredientResponse> getIngredientByName(@RequestParam String name) {
        return getIngredientService.execute(name)
            .map(ing -> ok(new IngredientResponse(ing.id().value().toString(), ing.name())))
            .orElseGet(() -> notFound().build());
    }

    public record CreateIngredientRequest(String name) {
    }

    public record IngredientResponse(String id, String name) {
    }
}
