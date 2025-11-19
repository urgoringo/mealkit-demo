package com.urgoringo.mealkit.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NullUnmarked;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistence entity for Order, mapped to the orders table.
 * NullUnmarked due to JPA initialization requirements.
 */
@NullUnmarked
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "order_recipes", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "recipe_id")
    private List<Long> recipeIds = new ArrayList<>();

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;
}
