package com.urgoringo.mealkit.domain;

import com.urgoringo.mealkit.mapper.CustomerMapper;
import com.urgoringo.mealkit.persistence.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Domain repository for Customer.
 * Transforms persistence entities to domain models using CustomerMapper.
 */
@NullMarked
@Repository
@RequiredArgsConstructor
public class CustomerDomainRepository {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public Customer save(Customer customer) {
        var entity = customerMapper.toEntity(customer);
        var savedEntity = customerRepository.save(entity);
        return customerMapper.toDomain(savedEntity);
    }

    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }

    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email)
                .map(customerMapper::toDomain);
    }
}
