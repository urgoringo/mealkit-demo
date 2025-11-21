package com.urgoringo.mealkit.service;

import org.jspecify.annotations.NullMarked;

/**
 * Interface for password hashing and verification operations.
 * In production, this uses BCrypt or similar secure hashing algorithm.
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

    /**
     * Verifies a plain text password against a hashed password.
     *
     * @param plainPassword the plain text password
     * @param hashedPassword the hashed password to verify against
     * @return true if the password matches, false otherwise
     */
    boolean verify(String plainPassword, String hashedPassword);
}
