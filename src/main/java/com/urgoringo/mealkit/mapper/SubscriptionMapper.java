package com.urgoringo.mealkit.mapper;

import com.urgoringo.mealkit.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.domain.Subscription;
import com.urgoringo.mealkit.persistence.SubscriptionEntity;
import org.mapstruct.Mapper;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * MapStruct mapper for converting between Subscription domain model and SubscriptionEntity.
 */
@NullMarked
@Mapper(componentModel = "spring", uses = {OrderMapper.class})
public interface SubscriptionMapper {

    Subscription toDomain(SubscriptionEntity entity);

    SubscriptionEntity toEntity(Subscription subscription);

    default Id<Subscription> mapSubscriptionId(@Nullable Long id) {
        return id == null ? Id.unassigned() : Id.of(id);
    }

    default Long mapSubscriptionId(Id<Subscription> id) {
        return id.value();
    }

    default Id<Customer> mapCustomerId(@Nullable Long id) {
        return id == null ? Id.unassigned() : Id.of(id);
    }

    default Long mapCustomerId(Id<Customer> id) {
        return id.value();
    }
}
