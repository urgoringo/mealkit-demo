package com.urgoringo.mealkit.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NullUnmarked;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistence entity for Subscription, mapped to the subscriptions table.
 * NullUnmarked due to JPA initialization requirements.
 */
@NullUnmarked
@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "subscription_id")
    private List<OrderEntity> upcomingOrders = new ArrayList<>();

    @Column(name = "delivery_address", nullable = false, columnDefinition = "TEXT")
    private String deliveryAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_day", length = 10)
    private DayOfWeek deliveryDay;
}
