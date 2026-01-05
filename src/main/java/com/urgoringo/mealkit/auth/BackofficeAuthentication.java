package com.urgoringo.mealkit.auth;

import com.urgoringo.mealkit.backoffice.domain.BackofficeUser;
import com.urgoringo.mealkit.domain.Id;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class BackofficeAuthentication extends AbstractAuthenticationToken {

    private final Id<BackofficeUser> userId;

    public BackofficeAuthentication(Id<BackofficeUser> userId) {
        super(List.of(new SimpleGrantedAuthority("ROLE_BACKOFFICE")));
        this.userId = userId;
        setAuthenticated(true);
    }

    @Override
    public Id<BackofficeUser> getPrincipal() {
        return userId;
    }

    @Override
    public @Nullable Object getCredentials() {
        return null;
    }

    @Override
    public String getName() {
        return userId.value().toString();
    }
}
