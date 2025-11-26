package com.urgoringo.mealkit.subscription.api;

import com.urgoringo.mealkit.subscription.api.SubscriptionController.OrderResponse;
import com.urgoringo.mealkit.subscription.api.SubscriptionController.SubscriptionResponse;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.subscription.domain.Order;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import com.urgoringo.mealkit.subscription.domain.Subscription;
import org.mapstruct.Mapper;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
@Mapper(componentModel = "spring")
public interface SubscriptionApiMapper {

    SubscriptionResponse toResponse(Subscription subscription);

    OrderResponse toOrderResponse(Order order);

    default Long mapCustomerId(Id<?> id) {
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
