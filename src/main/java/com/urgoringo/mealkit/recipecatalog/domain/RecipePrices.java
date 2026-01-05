package com.urgoringo.mealkit.recipecatalog.domain;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.urgoringo.mealkit.domain.Money;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RecipePrices {

    private final RecipePricingCategoriesRepository repository;
    private final LoadingCache<PricingCategory, Money> cache;

    public RecipePrices(RecipePricingCategoriesRepository repository) {
        this.repository = repository;
        this.cache = Caffeine.newBuilder()
            .refreshAfterWrite(1, TimeUnit.MINUTES)
            .build(repository::findPriceByCategory);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        warmupCache();
    }

    private void warmupCache() {
        cache.putAll(repository.findAll());
    }

    public Money by(PricingCategory category) {
        return cache.get(category);
    }

}
