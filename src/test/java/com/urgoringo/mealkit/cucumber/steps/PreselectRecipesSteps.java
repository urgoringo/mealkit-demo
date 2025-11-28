package com.urgoringo.mealkit.cucumber.steps;

import com.urgoringo.mealkit.cucumber.ApplicationRunner;
import com.urgoringo.mealkit.cucumber.ApplicationRunner.SubscriptionResponse;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static com.urgoringo.mealkit.cucumber.ApplicationRunner.SubscriptionRequest.aSubscription;
import static com.urgoringo.mealkit.cucumber.scaffolding.TestFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RequiredArgsConstructor
public class PreselectRecipesSteps {

    private final ApplicationRunner app;
    private SubscriptionResponse subscription;
    private LocalDate currentDay;
    private String customerEmail;
    private String customerPassword;
    private String authToken;

    @Before
    public void cleanupDatabase() {
        app.deleteAllSubscriptions();
        app.deleteAllRecipes();
    }

    @Given("a subscription exists where upcoming order has delivery date {date}")
    public void givenSubscriptionExistsWithUpcomingOrderDeliveryDate(LocalDate deliveryDate) {
        customerEmail = anEmail();
        customerPassword = aPassword();

        app.signupCustomer(customerEmail, customerPassword).expectSuccess();

        List<Long> recipeIds = app.havingRecipes(3);

        // Calculate what day to freeze time to so that next occurrence of delivery day equals deliveryDate
        // For 2025-11-24 (Monday), freeze time to 2025-11-18 (Tuesday) so next Monday is 2025-11-24
        DayOfWeek deliveryDay = deliveryDate.getDayOfWeek();
        LocalDate signupDate = deliveryDate.minusDays(6); // 6 days before delivery
        app.freezeTimeOn(signupDate);

        subscription = app.signup(
                aSubscription(recipeIds)
                        .withCustomerEmail(customerEmail)
                        .withDeliveryDay(deliveryDay)
        ).expectSuccess();

        authToken = app.loginCustomer(customerEmail, customerPassword).expectSuccess().token();

        assertNotNull(subscription, "Subscription should not be null");
        assertNotNull(subscription.upcomingOrders(), "Upcoming orders should not be null");
        assertEquals(1, subscription.upcomingOrders().size(), "Should have exactly one upcoming order");
        assertEquals(deliveryDate, subscription.upcomingOrders().get(0).deliveryDate(),
                "First order should have delivery date " + deliveryDate);
    }

    @When("current day becomes {date}")
    public void whenCurrentDayBecomes(LocalDate date) {
        currentDay = date;

        app.freezeTimeOn(currentDay);

        app.processSubscriptionOrders(subscription.id());

        subscription = app.getCustomerSubscription(authToken).expectSuccess();
    }

    @Then("system adds new upcoming order with delivery date {date}")
    public void thenSystemAddsNewUpcomingOrderWithDeliveryDate(LocalDate expectedDeliveryDate) {
        assertNotNull(subscription, "Subscription should not be null");
        assertNotNull(subscription.upcomingOrders(), "Upcoming orders should not be null");
        assertEquals(2, subscription.upcomingOrders().size(),
                "Should have exactly two upcoming orders after processing");

        var secondOrder = subscription.upcomingOrders().get(1);
        assertNotNull(secondOrder.deliveryDate(), "Second order delivery date should not be null");
        assertEquals(expectedDeliveryDate, secondOrder.deliveryDate(),
                "Second order should have delivery date " + expectedDeliveryDate);
    }
}
