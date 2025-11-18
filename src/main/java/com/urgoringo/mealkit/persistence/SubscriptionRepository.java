package com.urgoringo.mealkit.persistence;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for SubscriptionEntity.
 */
@NullMarked
@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
}
