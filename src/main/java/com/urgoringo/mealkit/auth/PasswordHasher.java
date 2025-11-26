package com.urgoringo.mealkit.auth;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PasswordHasher {

    String hash(String plainPassword);

    boolean verify(String plainPassword, String hashedPassword);
}
