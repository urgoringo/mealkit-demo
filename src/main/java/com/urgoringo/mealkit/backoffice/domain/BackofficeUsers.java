package com.urgoringo.mealkit.backoffice.domain;

import com.urgoringo.mealkit.domain.Id;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.urgoringo.mealkit.jooq.tables.BackofficeUsers.BACKOFFICE_USERS;

@NullMarked
@Repository
@RequiredArgsConstructor
public class BackofficeUsers {

    private final DSLContext dsl;

    @Transactional
    public BackofficeUser save(BackofficeUser user) {
        if (user.id().isAssigned()) {
            dsl.update(BACKOFFICE_USERS)
                    .set(BACKOFFICE_USERS.EMAIL, user.email())
                    .set(BACKOFFICE_USERS.PASSWORD, user.password())
                    .where(BACKOFFICE_USERS.ID.eq(user.id().value()))
                    .execute();
            return user;
        } else {
            var record = dsl.insertInto(BACKOFFICE_USERS)
                    .set(BACKOFFICE_USERS.EMAIL, user.email())
                    .set(BACKOFFICE_USERS.PASSWORD, user.password())
                    .returning(BACKOFFICE_USERS.ID)
                    .fetchOne();
            if (record == null) {
                throw new IllegalStateException("Failed to insert backoffice user");
            }
            return new BackofficeUser(
                    Id.of(record.getId()),
                    user.email(),
                    user.password()
            );
        }
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
