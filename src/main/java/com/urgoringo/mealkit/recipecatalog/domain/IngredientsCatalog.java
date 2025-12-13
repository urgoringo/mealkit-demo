package com.urgoringo.mealkit.recipecatalog.domain;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.urgoringo.mealkit.domain.Id;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@NullMarked
@Component
@RequiredArgsConstructor
public class IngredientsCatalog {

    private final IngredientsCatalogRepository repository;
    @Nullable
    private LoadingCache<String, Map<Id<Ingredient>, Ingredient>> cache;

    @PostConstruct
    public void initializeCache() {
        this.cache = Caffeine.newBuilder()
                .refreshAfterWrite(1, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public Map<Id<Ingredient>, Ingredient> load(String key) {
                        return loadAllIngredients();
                    }

                    @Override
                    public Map<Id<Ingredient>, Ingredient> reload(String key, Map<Id<Ingredient>, Ingredient> oldValue) {
                        return loadAllIngredients();
                    }
                });
    }

    private Map<Id<Ingredient>, Ingredient> loadAllIngredients() {
        List<Ingredient> ingredients = repository.findAll();
        return ingredients.stream()
                .collect(Collectors.toMap(Ingredient::id, ingredient -> ingredient));
    }

    private LoadingCache<String, Map<Id<Ingredient>, Ingredient>> getCache() {
        if (cache == null) {
            throw new IllegalStateException("Cache not initialized");
        }
        return cache;
    }

    public List<Ingredient> findAll() {
        Map<Id<Ingredient>, Ingredient> ingredients = getCache().get("all");
        return List.copyOf(ingredients.values());
    }

    @Nullable
    public Ingredient getById(Id<Ingredient> id) {
        Map<Id<Ingredient>, Ingredient> ingredients = getCache().get("all");
        return ingredients.get(id);
    }

    @Nullable
    public Ingredient findByName(String name) {
        return repository.findByName(name);
    }

    public Ingredient save(Ingredient ingredient) {
        Ingredient saved = repository.save(ingredient);
        getCache().invalidateAll();
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
        getCache().invalidateAll();
    }
}
