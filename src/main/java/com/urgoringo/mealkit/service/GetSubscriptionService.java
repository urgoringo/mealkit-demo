package com.urgoringo.mealkit.service;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.domain.Subscription;
import com.urgoringo.mealkit.domain.SubscriptionDomainRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for retrieving a subscription by ID.
 * Follows DDD principle: one service per use case.
 */
@NullMarked
@Service
@RequiredArgsConstructor
public class GetSubscriptionService {

    private final SubscriptionDomainRepository subscriptionDomainRepository;

    @Transactional(readOnly = true)
    public Subscription execute(Id<Subscription> subscriptionId) {
        return subscriptionDomainRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found: " + subscriptionId.value()));
    }
}
