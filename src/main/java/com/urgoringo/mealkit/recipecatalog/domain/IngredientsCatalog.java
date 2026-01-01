package com.urgoringo.mealkit.recipecatalog.domain;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.urgoringo.mealkit.domain.Id;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toMap;

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

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        warmupCache();
    }

    private void warmupCache() {
        List<Ingredient> allIngredients = repository.findAll();
        cache.putAll(allIngredients.stream()
            .collect(toMap(Ingredient::id, ingredient -> ingredient)));
    }

    public Ingredient getById(Id<Ingredient> id) {
        return cache.get(id);
    }

    public Optional<Ingredient> findByName(String name) {
        return repository.findByName(name);
    }

    public Ingredient add(Ingredient ingredient) {
        Ingredient saved = repository.add(ingredient);
        cache.put(saved.id(), saved);
        return saved;
    }

    public Ingredient findOrAdd(Ingredient ingredient) {
        return repository.findByName(ingredient.name()).orElseGet(() -> add(ingredient));
    }

    public void deleteAll() {
        repository.deleteAll();
        cache.invalidateAll();
    }
}
