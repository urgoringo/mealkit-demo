package com.urgoringo.mealkit.persistence;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for RecipeEntity.
 */
@NullMarked
@Repository
public interface RecipeRepository extends JpaRepository<RecipeEntity, Long> {
}
