package com.urgoringo.mealkit.auth;

import com.urgoringo.mealkit.domain.Id;
import org.jspecify.annotations.NullMarked;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Converts JWT tokens to appropriate Authentication by extracting role and ID.
 * Supports both customer and backoffice user authentication.
 */
@NullMarked
@Component
public class CustomerJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String role = jwt.getClaimAsString("role");
        
        if ("BACKOFFICE".equals(role)) {
            return new BackofficeAuthentication(jwt.getSubject());
        }
        
        Long customerId = Long.parseLong(jwt.getSubject());
        return new CustomerAuthentication(Id.of(customerId));
    }
}
