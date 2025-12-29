package com.urgoringo.mealkit

import java.time.LocalDate

import static com.urgoringo.mealkit.scaffolding.TestFactory.aSubscription
import static java.time.DayOfWeek.MONDAY
import static java.time.DayOfWeek.TUESDAY

class UpdateSubscriptionSpec extends ApplicationSpecification {
    def "customer can update delivery day for subscription"() {
        given: "customer has a subscription with delivery day Monday (2025.11.24)"
            def today = LocalDate.of(2025, 11, 19)
            app.freezeTimeOn(today)
            def customer = app.havingCustomer()
            customer.havingSubscription(MONDAY)

        when: "customer updates delivery day to Tuesday"
            def response = app.updateSubscriptionDeliveryDay(customer.authToken, TUESDAY)
            def updatedSubscription = response.expectSuccess()

        then: "subscription delivery day is Tuesday (2025.11.25)"
            updatedSubscription.deliveryDay() == TUESDAY
            def firstOrder = updatedSubscription.upcomingOrders().first
            firstOrder.deliveryDate() == LocalDate.of(2025, 11, 25)
    }
}