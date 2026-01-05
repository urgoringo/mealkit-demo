package com.urgoringo.mealkit.auth;

import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.List;

/**
 * Spring Security Authentication implementation that wraps a Customer ID.
 * This allows @AuthenticationPrincipal to inject Id<Customer> directly into controllers.
 */
public class CustomerAuthentication extends AbstractAuthenticationToken {

    private final Id<Customer> customerId;

    public CustomerAuthentication(Id<Customer> customerId) {
        super(List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_CUSTOMER")));
        this.customerId = customerId;
        setAuthenticated(true);
    }

    @Override
    public Id<Customer> getPrincipal() {
        return customerId;
    }

    @Override
    public @Nullable Object getCredentials() {
        return null;
    }

    @Override
    public String getName() {
        return customerId.value().toString();
    }
}
