package com.urgoringo.mealkit.mealkit9.mealkit2

import com.urgoringo.mealkit.ApplicationSpecification

import java.time.LocalDate

import static com.urgoringo.mealkit.subscription.domain.OrderStatus.LOCKED
import static com.urgoringo.mealkit.subscription.domain.OrderStatus.PENDING

class OrderLockingSpec extends ApplicationSpecification {

    def "order is locked 3 days before delivery"() {
        given: "a subscription with an upcoming order on 2125-11-24"
            def customer = app.havingCustomer()
            app.freezeTimeOn(LocalDate.parse("2125-11-20"))
            customer.having(LocalDate.parse("2125-11-24").dayOfWeek).get()

        when: "date is 2125-11-21"
            app.freezeTimeOn(LocalDate.parse("2125-11-21"))

        and: "customer views their subscription"
            def subscription = app.getCustomerSubscription(customer.authToken).expectSuccess()

        then: "upcoming order is locked"
            subscription.upcomingOrders().first.status() == LOCKED
    }

    def "order is not locked if delivery is more than 3 days away"() {
        given: "a subscription with an upcoming order on 2125-11-24"
            def customer = app.havingCustomer()
            app.freezeTimeOn(LocalDate.parse("2125-11-20"))
            customer.having(LocalDate.parse("2125-11-24").dayOfWeek).get()

        when: "date is 2025-11-20"
            app.freezeTimeOn(LocalDate.parse("2025-11-20"))

        and: "customer views their subscription"
            def subscription = app.getCustomerSubscription(customer.authToken).expectSuccess()

        then: "upcoming order is pending"
            subscription.upcomingOrders().first.status() == PENDING
    }

    def "locked order cannot be updated"() {
        given: "a subscription with a locked order"
            def customer = app.havingCustomer()
            def order = customer.havingSubscription().withNextUpcomingOrder().locked().get()

        when: "customer tries to update their upcoming order"
            def recipeIds = app.getRecipes(3)
            def response = app.updateUpcomingOrderRecipes(customer.authToken, order.id(), recipeIds)

        then: "system returns 422"
            response.expectError() == 422
    }
}