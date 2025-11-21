package com.urgoringo.mealkit.domain;

import com.urgoringo.mealkit.mapper.SubscriptionMapper;
import com.urgoringo.mealkit.persistence.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Domain repository for Subscription.
 * Transforms persistence entities to domain models using SubscriptionMapper.
 */
@NullMarked
@Repository
@RequiredArgsConstructor
public class SubscriptionDomainRepository {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;

    public Subscription save(Subscription subscription) {
        var entity = subscriptionMapper.toEntity(subscription);
        var savedEntity = subscriptionRepository.save(entity);
        return subscriptionMapper.toDomain(savedEntity);
    }

    public Optional<Subscription> findById(Id<Subscription> id) {
        return subscriptionRepository.findById(id.value())
                .map(subscriptionMapper::toDomain);
    }

    public Optional<Subscription> findByCustomerId(Id<Customer> customerId) {
        return subscriptionRepository.findByCustomerId(customerId.value())
                .map(subscriptionMapper::toDomain);
    }
}
