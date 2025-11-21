package com.urgoringo.mealkit.mapper;

import com.urgoringo.mealkit.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.persistence.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * MapStruct mapper for converting between Customer domain model and CustomerEntity.
 */
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
