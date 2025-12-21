package com.urgoringo.mealkit.auth;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@NullMarked
public class BackofficeAuthentication extends AbstractAuthenticationToken {

    private final String userId;

    public BackofficeAuthentication(String userId) {
        super(List.of(new SimpleGrantedAuthority("ROLE_BACKOFFICE")));
        this.userId = userId;
        setAuthenticated(true);
    }

    @Override
    public String getPrincipal() {
        return userId;
    }

    @Override
    public @Nullable Object getCredentials() {
        return null;
    }

    @Override
    public String getName() {
        return userId;
    }
}
