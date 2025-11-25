package com.urgoringo.mealkit.persistence;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@NullMarked
@Repository
public interface RecipeJpaRepository extends JpaRepository<RecipeEntity, Long> {
}
