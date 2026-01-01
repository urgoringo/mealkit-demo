package com.urgoringo.mealkit.backoffice.domain;

import com.urgoringo.mealkit.domain.Id;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record BackofficeUser(
        Id<BackofficeUser> id,
        String email,
        String password
) {
    public static BackofficeUser create(String email, String password) {
        return new BackofficeUser(Id.generate(), email, password);
    }
}
