package com.urgoringo.mealkit.subscription.persistence;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.subscription.domain.Order;
import com.urgoringo.mealkit.recipecatalog.domain.Recipe;
import org.mapstruct.Mapper;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
@Mapper(componentModel = "spring")
public interface OrderMapper {

    Order toDomain(OrderEntity entity);

    List<Order> toDomain(List<OrderEntity> entities);

    OrderEntity toEntity(Order order);

    List<OrderEntity> toEntity(List<Order> orders);

    default Id<Order> mapOrderId(@Nullable Long id) {
        return id == null ? Id.unassigned() : Id.of(id);
    }

    default Long mapOrderId(Id<Order> id) {
        return id.value();
    }

    default List<Id<Recipe>> mapRecipeIds(List<Long> ids) {
        return ids.stream()
                .map(id -> Id.<Recipe>of(id))
                .toList();
    }

    default List<Long> mapRecipeIdsToLong(List<Id<Recipe>> ids) {
        return ids.stream()
                .map(Id::value)
                .toList();
    }
}
