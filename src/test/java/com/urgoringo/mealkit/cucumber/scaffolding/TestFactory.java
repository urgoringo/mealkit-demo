package com.urgoringo.mealkit.cucumber.scaffolding;

import java.util.Random;

public class TestFactory {

    private static final Random random = new Random();

    public static String anEmail() {
        return "test-customer-" + random.nextLong() + "@example.com";
    }

    public static String aPassword() {
        return "TestPassword123!";
    }
}
