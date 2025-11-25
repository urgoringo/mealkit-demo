package com.urgoringo.mealkit.persistence;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@NullMarked
@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, Long> {

    boolean existsByEmail(String email);

    Optional<CustomerEntity> findByEmail(String email);
}
