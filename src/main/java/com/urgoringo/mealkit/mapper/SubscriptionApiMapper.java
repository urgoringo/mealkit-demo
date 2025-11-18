package com.urgoringo.mealkit.mapper;

import com.urgoringo.mealkit.controller.SubscriptionController.OrderResponse;
import com.urgoringo.mealkit.controller.SubscriptionController.SubscriptionResponse;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.domain.Order;
import com.urgoringo.mealkit.domain.Recipe;
import com.urgoringo.mealkit.domain.Subscription;
import org.mapstruct.Mapper;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * MapStruct mapper for converting between Subscription API DTOs and domain models.
 */
@NullMarked
@Mapper(componentModel = "spring")
public interface SubscriptionApiMapper {

    SubscriptionResponse toResponse(Subscription subscription);

    OrderResponse toOrderResponse(Order order);

    default Long mapSubscriptionId(Id<Subscription> id) {
        return id.value();
    }

    default Long mapCustomerId(Id<?> id) {
        return id.value();
    }

    default Long mapOrderId(Id<Order> id) {
        return id.value();
    }

    default Long mapRecipeId(Id<Recipe> id) {
        return id.value();
    }

    default List<Id<Recipe>> mapRecipeIds(List<Long> ids) {
        return ids.stream()
                .map(Id::<Recipe>of)
                .toList();
    }

    default List<Long> mapRecipeIdsToLong(List<Id<Recipe>> ids) {
        return ids.stream()
                .map(Id::value)
                .toList();
    }
}
