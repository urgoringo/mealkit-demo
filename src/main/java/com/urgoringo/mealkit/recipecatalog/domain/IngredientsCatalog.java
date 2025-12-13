package com.urgoringo.mealkit.recipecatalog.domain;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.urgoringo.mealkit.domain.Id;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@NullMarked
@Component
public class IngredientsCatalog {

    private final IngredientsCatalogRepository repository;
    private final LoadingCache<Id<Ingredient>, Ingredient> cache;

    public IngredientsCatalog(IngredientsCatalogRepository repository) {
        this.repository = repository;
        this.cache = Caffeine.newBuilder()
            .refreshAfterWrite(1, TimeUnit.MINUTES)
            .build(id -> {
                Ingredient ingredient = repository.findById(id);
                if (ingredient == null) {
                    throw new IllegalStateException("Ingredient not found: " + id);
                }
                return ingredient;
            });
    }

    public List<Ingredient> findAll() {
        return repository.findAll();
    }

    public Ingredient getById(Id<Ingredient> id) {
        return cache.get(id);
    }

    @Nullable
    public Ingredient findByName(String name) {
        return repository.findByName(name);
    }

    public Ingredient save(Ingredient ingredient) {
        Ingredient saved = repository.save(ingredient);
        if (saved.id().isAssigned()) {
            cache.invalidate(saved.id());
        }
        return saved;
    }

    public Ingredient findOrCreate(String name) {
        Ingredient existing = findByName(name);
        if (existing != null) {
            return existing;
        }
        return save(Ingredient.create(name));
    }

    public void deleteAll() {
        repository.deleteAll();
        cache.invalidateAll();
    }
}
