package com.urgoringo.mealkit.subscription.persistence;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@NullMarked
@Repository
public interface SubscriptionJpaRepository extends JpaRepository<SubscriptionEntity, Long> {
    Optional<SubscriptionEntity> findByCustomerId(Long customerId);
    
    @Query("SELECT s.id FROM SubscriptionEntity s")
    List<Long> findAllIds();
}
