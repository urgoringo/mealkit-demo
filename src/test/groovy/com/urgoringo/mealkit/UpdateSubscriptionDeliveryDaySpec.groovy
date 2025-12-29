package com.urgoringo.mealkit

import java.time.LocalDate

import static java.time.DayOfWeek.MONDAY
import static java.time.DayOfWeek.TUESDAY
import static java.time.DayOfWeek.WEDNESDAY

class UpdateSubscriptionDeliveryDaySpec extends ApplicationSpecification {
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

    def "do not update upcoming order delivery date when it has already been changed by customer"() {
        given: "customer has a subscription with delivery day Monday (2025.11.24)"
            app.freezeTimeOn(LocalDate.of(2025, 11, 19))
            def customer = app.havingCustomer()
            customer.havingSubscription(MONDAY).withNextUpcomingOrder().deliveryDayChangedTo(WEDNESDAY)

        when: "customer updates delivery day to Tuesday"
            def response = app.updateSubscriptionDeliveryDay(customer.authToken, TUESDAY)
            def updatedSubscription = response.expectSuccess()

        then: "upcoming order delivery day is still Wednesday (2025.11.26)"
            def firstOrder = updatedSubscription.upcomingOrders().first
            firstOrder.deliveryDate() == LocalDate.of(2025, 11, 26)

        and: "subscription delivery day is Tuesday"
            updatedSubscription.deliveryDay() == TUESDAY
    }
}