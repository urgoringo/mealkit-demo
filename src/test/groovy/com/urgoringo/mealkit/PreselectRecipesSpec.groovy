package com.urgoringo.mealkit

import com.urgoringo.mealkit.scaffolding.ApplicationRunner
import com.urgoringo.mealkit.scaffolding.ApplicationSetup
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDate

import static com.urgoringo.mealkit.scaffolding.ApplicationRunner.SubscriptionRequest.aSubscription
import static com.urgoringo.mealkit.scaffolding.TestFactory.aPassword
import static com.urgoringo.mealkit.scaffolding.TestFactory.anEmail

class PreselectRecipesSpec extends ApplicationSpecification {

    @Autowired
    ApplicationRunner app

    @Autowired
    ApplicationSetup setup

    def setup() {
        app.deleteAllSubscriptions()
        app.deleteAllRecipes()
    }

    def "select recipes for the next upcoming order"() {
        given: "a subscription exists where upcoming order has delivery date 2025.11.24"
            def signupDate = LocalDate.parse("2025-11-18")
            def deliveryDate = LocalDate.parse("2025-11-24")

            setup
                    .freezeTimeOn(signupDate)
                    .havingSubscription(deliveryDate.getDayOfWeek())

        when: "current day becomes 2025.11.21"
            def currentDate = LocalDate.parse("2025-11-21")
            app
                    .freezeTimeOn(currentDate)
                    .processSubscriptionOrders()
            def subscription = setup.customerSubscription

        then: "system adds new upcoming order with delivery date 2025.12.01"
            subscription.upcomingOrders().size() == 2

            def expectedDeliveryDate = LocalDate.parse("2025-12-01")
            def secondOrder = subscription.upcomingOrders().get(1)
            secondOrder.deliveryDate() == expectedDeliveryDate
    }

}
