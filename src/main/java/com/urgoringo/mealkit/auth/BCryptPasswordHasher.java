package com.urgoringo.mealkit.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Production-grade password hasher using BCrypt.
 * BCrypt is specifically designed for password hashing with built-in salt and configurable work factor.
 */
@Component
public class BCryptPasswordHasher implements PasswordHasher {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String hash(String plainPassword) {
        String encoded = encoder.encode(plainPassword);
        if (encoded == null) {
            throw new IllegalStateException("BCrypt encoder returned null for non-null input");
        }
        return encoded;
    }

    @Override
    public boolean verify(String plainPassword, String hashedPassword) {
        return encoder.matches(plainPassword, hashedPassword);
    }
}
