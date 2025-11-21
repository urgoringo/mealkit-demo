package com.urgoringo.mealkit.service;

import org.jspecify.annotations.NullMarked;

/**
 * Interface for password hashing operations.
 * In production, this would use BCrypt or similar secure hashing algorithm.
 */
@NullMarked
public interface PasswordHasher {

    /**
     * Hashes a plain text password.
     *
     * @param plainPassword the plain text password
     * @return the hashed password
     */
    String hash(String plainPassword);
}
