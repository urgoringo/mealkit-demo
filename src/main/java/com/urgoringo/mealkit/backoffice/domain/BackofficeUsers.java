package com.urgoringo.mealkit.backoffice.domain;

import com.urgoringo.mealkit.domain.Id;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.urgoringo.mealkit.jooq.tables.BackofficeUsers.BACKOFFICE_USERS;

@Repository
@RequiredArgsConstructor
public class BackofficeUsers {

    private final DSLContext dsl;

    @Transactional
    public BackofficeUser add(BackofficeUser user) {
        dsl.insertInto(BACKOFFICE_USERS)
                .set(BACKOFFICE_USERS.ID, user.id().value())
                .set(BACKOFFICE_USERS.EMAIL, user.email())
                .set(BACKOFFICE_USERS.PASSWORD, user.password())
                .execute();
        return user;
    }

    @Transactional
    public BackofficeUser update(BackofficeUser user) {
        dsl.update(BACKOFFICE_USERS)
                .set(BACKOFFICE_USERS.EMAIL, user.email())
                .set(BACKOFFICE_USERS.PASSWORD, user.password())
                .where(BACKOFFICE_USERS.ID.eq(user.id().value()))
                .execute();
        return user;
    }

    public Optional<BackofficeUser> findByEmail(String email) {
        return dsl.selectFrom(BACKOFFICE_USERS)
                .where(BACKOFFICE_USERS.EMAIL.eq(email))
                .fetchOptional()
                .map(record -> new BackofficeUser(
                        Id.of(record.getId()),
                        record.getEmail(),
                        record.getPassword()
                ));
    }

    @Transactional
    public void deleteAll() {
        dsl.deleteFrom(BACKOFFICE_USERS).execute();
    }
}
