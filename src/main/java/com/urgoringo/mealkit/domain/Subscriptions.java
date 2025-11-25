package com.urgoringo.mealkit.domain;

import com.urgoringo.mealkit.mapper.SubscriptionMapper;
import com.urgoringo.mealkit.persistence.SubscriptionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@NullMarked
@Repository
@RequiredArgsConstructor
public class Subscriptions {

    private final SubscriptionJpaRepository subscriptionJpaRepository;
    private final SubscriptionMapper subscriptionMapper;

    public Subscription save(Subscription subscription) {
        var entity = subscriptionMapper.toEntity(subscription);
        var savedEntity = subscriptionJpaRepository.save(entity);
        return subscriptionMapper.toDomain(savedEntity);
    }

    public Optional<Subscription> findById(Id<Subscription> id) {
        return subscriptionJpaRepository.findById(id.value())
                .map(subscriptionMapper::toDomain);
    }

    public Optional<Subscription> findByCustomerId(Id<Customer> customerId) {
        return subscriptionJpaRepository.findByCustomerId(customerId.value())
                .map(subscriptionMapper::toDomain);
    }
}
