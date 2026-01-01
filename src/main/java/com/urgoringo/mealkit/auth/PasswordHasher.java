package com.urgoringo.mealkit.auth;
public interface PasswordHasher {

    String hash(String plainPassword);

    boolean verify(String plainPassword, String hashedPassword);
}
