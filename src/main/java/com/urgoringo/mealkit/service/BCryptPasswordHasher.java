package com.urgoringo.mealkit.service;

import org.jspecify.annotations.NullMarked;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Production-grade password hasher using BCrypt.
 * BCrypt is specifically designed for password hashing with built-in salt and configurable work factor.
 */
@NullMarked
@Component
public class BCryptPasswordHasher implements PasswordHasher {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String hash(String plainPassword) {
        return encoder.encode(plainPassword);
    }

    @Override
    public boolean verify(String plainPassword, String hashedPassword) {
        return encoder.matches(plainPassword, hashedPassword);
    }
}
