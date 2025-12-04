package com.urgoringo.mealkit

import com.urgoringo.mealkit.cucumber.ApplicationRunner
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDate

import static com.urgoringo.mealkit.cucumber.ApplicationRunner.SubscriptionRequest.aSubscription
import static com.urgoringo.mealkit.cucumber.scaffolding.TestFactory.aPassword
import static com.urgoringo.mealkit.cucumber.scaffolding.TestFactory.anEmail

class PreselectRecipesSpec extends ApplicationSpecification {

    @Autowired
    ApplicationRunner app

    def setup() {
        app.deleteAllSubscriptions()
        app.deleteAllRecipes()
    }

    def "select recipes for the next upcoming order"() {
        given: "a subscription exists where upcoming order has delivery date 2025.11.24"
            def deliveryDate = LocalDate.of(2025, 11, 24)
            def recipeIds = app.havingRecipes(3)

            def deliveryDay = deliveryDate.getDayOfWeek()
            def signupDate = deliveryDate.minusDays(6)
            app.freezeTimeOn(signupDate)

            def signupResponse = app.signupCustomer(anEmail(), aPassword()).expectSuccess()
            def authToken = signupResponse.token()

            def subscription = app.create(
                    authToken,
                    aSubscription(recipeIds).withDeliveryDay(deliveryDay)
            ).expectSuccess()

            assert subscription != null
            assert subscription.upcomingOrders() != null
            assert subscription.upcomingOrders().size() == 1
            assert subscription.upcomingOrders().get(0).deliveryDate() == deliveryDate

        when: "current day becomes 2025.11.21"
            def currentDate = LocalDate.of(2025, 11, 21)
            app.freezeTimeOn(currentDate)
            app.processSubscriptionOrders(subscription.id())
            subscription = app.getCustomerSubscription(authToken).expectSuccess()

        then: "system adds new upcoming order with delivery date 2025.12.01"
            def expectedDeliveryDate = LocalDate.of(2025, 12, 1)
            subscription != null
            subscription.upcomingOrders() != null
            subscription.upcomingOrders().size() == 2

            def secondOrder = subscription.upcomingOrders().get(1)
            secondOrder.deliveryDate() != null
            secondOrder.deliveryDate() == expectedDeliveryDate
    }
}
