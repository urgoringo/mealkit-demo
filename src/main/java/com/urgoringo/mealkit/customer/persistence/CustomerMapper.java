package com.urgoringo.mealkit.customer.persistence;

import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(source = "password", target = "hashedPassword")
    Customer toDomain(CustomerEntity entity);

    @Mapping(source = "hashedPassword", target = "password")
    CustomerEntity toEntity(Customer customer);

    default Id<Customer> mapId(@Nullable Long id) {
        return id == null ? Id.unassigned() : Id.of(id);
    }

    default Long mapId(Id<Customer> id) {
        return id.value();
    }
}
