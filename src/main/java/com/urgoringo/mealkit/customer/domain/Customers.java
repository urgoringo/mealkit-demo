package com.urgoringo.mealkit.customer.domain;

import com.urgoringo.mealkit.domain.Id;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.urgoringo.mealkit.jooq.tables.Customers.CUSTOMERS;

@NullMarked
@Repository
@RequiredArgsConstructor
public class Customers {

    private final DSLContext dsl;

    public Customer save(Customer customer) {
        if (customer.id().isAssigned()) {
            dsl.update(CUSTOMERS)
                    .set(CUSTOMERS.EMAIL, customer.email())
                    .set(CUSTOMERS.PASSWORD, customer.hashedPassword())
                    .where(CUSTOMERS.ID.eq(customer.id().value()))
                    .execute();
            return customer;
        } else {
            var record = dsl.insertInto(CUSTOMERS)
                    .set(CUSTOMERS.EMAIL, customer.email())
                    .set(CUSTOMERS.PASSWORD, customer.hashedPassword())
                    .returning(CUSTOMERS.ID)
                    .fetchOne();
            if (record == null) {
                throw new IllegalStateException("Failed to insert customer");
            }
            return new Customer(Id.of(record.getId()), customer.email(), customer.hashedPassword());
        }
    }

    public boolean existsByEmail(String email) {
        return dsl.fetchExists(
                dsl.selectFrom(CUSTOMERS)
                        .where(CUSTOMERS.EMAIL.eq(email))
        );
    }

    public Optional<Customer> findByEmail(String email) {
        return dsl.selectFrom(CUSTOMERS)
                .where(CUSTOMERS.EMAIL.eq(email))
                .fetchOptional()
                .map(record -> new Customer(
                        Id.of(record.getId()),
                        record.getEmail(),
                        record.getPassword()
                ));
    }

    public void deleteAll() {
        dsl.deleteFrom(CUSTOMERS).execute();
    }
}
