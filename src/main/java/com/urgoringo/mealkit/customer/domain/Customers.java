package com.urgoringo.mealkit.customer.domain;

import com.urgoringo.mealkit.customer.persistence.CustomerMapper;
import com.urgoringo.mealkit.customer.persistence.CustomerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@NullMarked
@Repository
@RequiredArgsConstructor
public class Customers {

    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerMapper customerMapper;

    public Customer save(Customer customer) {
        var entity = customerMapper.toEntity(customer);
        var savedEntity = customerJpaRepository.save(entity);
        return customerMapper.toDomain(savedEntity);
    }

    public boolean existsByEmail(String email) {
        return customerJpaRepository.existsByEmail(email);
    }

    public Optional<Customer> findByEmail(String email) {
        return customerJpaRepository.findByEmail(email)
                .map(customerMapper::toDomain);
    }
}
