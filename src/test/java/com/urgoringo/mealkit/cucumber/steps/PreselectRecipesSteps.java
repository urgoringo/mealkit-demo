package com.urgoringo.mealkit.cucumber.steps;

import com.urgoringo.mealkit.TestContainersConfiguration.TestClock;
import com.urgoringo.mealkit.cucumber.ApplicationRunner;
import com.urgoringo.mealkit.cucumber.ApplicationRunner.RecipeResponse;
import com.urgoringo.mealkit.cucumber.ApplicationRunner.SubscriptionResponse;
import lombok.RequiredArgsConstructor;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Step definitions for preselecting recipes for upcoming orders.
 */
@RequiredArgsConstructor
public class PreselectRecipesSteps {

    private final ApplicationRunner app;
    private final TestClock testClock;
    private SubscriptionResponse subscription;
    private LocalDate currentDay;
    private LocalDate expectedDeliveryDate;
    private String customerEmail;
    private String customerPassword;
    private String authToken;

    @Before
    public void cleanupDatabase() {
        // Clean up data before each scenario to ensure test isolation
        app.deleteAllSubscriptions();
        app.deleteAllRecipes();
    }

    @Given("a subscription exists where upcoming order has delivery date {date}")
    public void givenSubscriptionExistsWithUpcomingOrderDeliveryDate(LocalDate deliveryDate) {
        // Create a customer email and password for this test
        customerEmail = "test-customer-" + System.currentTimeMillis() + "@example.com";
        customerPassword = "TestPassword123!";

        // Sign up the customer
        app.signupCustomer(customerEmail, customerPassword).expectSuccess();

        // Create 3 recipes (minimum required)
        List<Long> recipeIds = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            RecipeResponse recipe = app.createRecipe("Recipe " + i).expectSuccess();
            recipeIds.add(recipe.id());
        }

        // Calculate what day to freeze time to so that next occurrence of delivery day equals deliveryDate
        // For 2025-11-24 (Monday), freeze time to 2025-11-18 (Tuesday) so next Monday is 2025-11-24
        DayOfWeek deliveryDay = deliveryDate.getDayOfWeek();
        LocalDate signupDate = deliveryDate.minusDays(6); // 6 days before delivery
        testClock.freezeTime(signupDate);

        // Create subscription with delivery day
        String defaultAddress = "123 Main St\n12345 New York\nUSA";
        subscription = app.createSubscription(customerEmail, recipeIds, defaultAddress, deliveryDay).expectSuccess();

        // Login to get authentication token for subsequent requests
        authToken = app.loginCustomer(customerEmail, customerPassword).expectSuccess().token();

        // Verify the subscription was created with the expected delivery date
        assertNotNull(subscription, "Subscription should not be null");
        assertNotNull(subscription.upcomingOrders(), "Upcoming orders should not be null");
        assertEquals(1, subscription.upcomingOrders().size(), "Should have exactly one upcoming order");
        assertEquals(deliveryDate, subscription.upcomingOrders().get(0).deliveryDate(),
                "First order should have delivery date " + deliveryDate);
    }

    @When("current day becomes {date}")
    public void whenCurrentDayBecomes(LocalDate date) {
        currentDay = date;

        // Freeze time to the current day
        testClock.freezeTime(currentDay);

        // Trigger the system to check for new orders to be added
        // Recipes will be automatically selected randomly by the system
        app.processSubscriptionOrders(subscription.id());

        // Reload the subscription to get the updated state using authenticated request
        subscription = app.getMySubscription(authToken).expectSuccess();
    }

    @Then("system adds new upcoming order with delivery date {date}")
    public void thenSystemAddsNewUpcomingOrderWithDeliveryDate(LocalDate expectedDeliveryDate) {
        // Verify subscription has two upcoming orders now
        assertNotNull(subscription, "Subscription should not be null");
        assertNotNull(subscription.upcomingOrders(), "Upcoming orders should not be null");
        assertEquals(2, subscription.upcomingOrders().size(),
                "Should have exactly two upcoming orders after processing");

        // Verify the second order has the expected delivery date
        var secondOrder = subscription.upcomingOrders().get(1);
        assertNotNull(secondOrder.deliveryDate(), "Second order delivery date should not be null");
        assertEquals(expectedDeliveryDate, secondOrder.deliveryDate(),
                "Second order should have delivery date " + expectedDeliveryDate);
    }
}
