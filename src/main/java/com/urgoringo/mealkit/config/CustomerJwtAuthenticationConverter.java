package com.urgoringo.mealkit.config;

import com.urgoringo.mealkit.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import org.jspecify.annotations.NullMarked;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Converts JWT tokens to CustomerAuthentication by extracting the customer ID.
 * This allows application code to work with Id<Customer> instead of raw JWT tokens.
 */
@NullMarked
@Component
public class CustomerJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Long customerId = Long.parseLong(jwt.getSubject());
        return new CustomerAuthentication(Id.of(customerId));
    }
}
