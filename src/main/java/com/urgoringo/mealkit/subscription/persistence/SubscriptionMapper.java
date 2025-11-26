package com.urgoringo.mealkit.subscription.persistence;

import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.subscription.domain.Subscription;
import org.mapstruct.Mapper;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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
