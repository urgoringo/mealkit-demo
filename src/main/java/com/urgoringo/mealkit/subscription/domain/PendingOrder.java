package com.urgoringo.mealkit.subscription.domain;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.domain.ValidationFailed;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import org.jspecify.annotations.NullMarked;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static com.urgoringo.mealkit.subscription.domain.OrderStatus.PENDING;
import static java.time.temporal.ChronoUnit.DAYS;

@NullMarked
public record PendingOrder(
    Id<Order> id,
    List<Id<Recipe>> recipeIds,
    LocalDate deliveryDate
) implements UpcomingOrder {
    private static final int MINIMUM_RECIPE_COUNT = 3;
    private static final int DAYS_BEFORE_DELIVERY_TO_LOCK = 3;

    public PendingOrder {
        if (recipeIds.size() < MINIMUM_RECIPE_COUNT) {
            throw new ValidationFailed("Order must contain at least " + MINIMUM_RECIPE_COUNT + " recipes");
        }
    }

    public static PendingOrder placed(List<Id<Recipe>> recipeIds, LocalDate deliveryDate) {
        return new PendingOrder(Id.unassigned(), recipeIds, deliveryDate);
    }

    public PendingOrder withUpdatedRecipes(List<Id<Recipe>> recipeIds) {
        return new PendingOrder(id, recipeIds, deliveryDate);
    }

    public PendingOrder withUpdatedDeliveryDate(LocalDate newDeliveryDate) {
        return new PendingOrder(id, recipeIds, newDeliveryDate);
    }

    public LockedOrder locked() {
        return new LockedOrder(Id.of(id.value()), recipeIds, deliveryDate);
    }

    public boolean shouldBeLocked(Clock clock) {
        LocalDate currentDate = LocalDate.now(clock);
        long daysUntilDelivery = currentDate.until(deliveryDate, DAYS);

        return daysUntilDelivery <= DAYS_BEFORE_DELIVERY_TO_LOCK;
    }

    @Override
    public OrderStatus status() {
        return PENDING;
    }

    @Override
    public boolean isLocked() {
        return false;
    }
}
