package com.urgoringo.mealkit

import com.urgoringo.mealkit.subscription.domain.OrderStatus
import spock.lang.PendingFeature

import java.time.LocalDate

import static com.urgoringo.mealkit.subscription.domain.OrderStatus.*

class OrderLockingSpec extends ApplicationSpecification {

    def "order is locked 3 days before delivery"() {
        given: "a subscription with an upcoming order on 2025-11-24"
            def customer = app.havingCustomer()
            app.freezeTimeOn(LocalDate.parse("2025-11-20"))
            customer.havingSubscription(LocalDate.parse("2025-11-24").dayOfWeek).get()

        when: "date is 2025-11-21"
            app.freezeTimeOn(LocalDate.parse("2025-11-21"))

        and: "customer views their subscription"
            def subscription = app.getCustomerSubscription(customer.authToken).expectSuccess()

        then: "upcoming order is locked"
            subscription.upcomingOrders().first.status() == LOCKED
    }

    def "order is not locked if delivery is more than 3 days away"() {
        given: "a subscription with an upcoming order on 2025-11-24"
            def customer = app.havingCustomer()
            app.freezeTimeOn(LocalDate.parse("2025-11-20"))
            customer.havingSubscription(LocalDate.parse("2025-11-24").dayOfWeek).get()

        when: "date is 2025-11-20"
            app.freezeTimeOn(LocalDate.parse("2025-11-20"))

        and: "customer views their subscription"
            def subscription = app.getCustomerSubscription(customer.authToken).expectSuccess()

        then: "upcoming order is pending"
            subscription.upcomingOrders().first.status() == PENDING
    }

    @PendingFeature
    def "locked order cannot be updated"() {
        given: "a subscription with a locked order"
            def customer = app.havingCustomer()
            customer.havingSubscription().withOrderLocked()

        when: "customer tries to update their subscription"
            def response = customer.update(customer.authToken, aSubscription().withDeliveryDay(SUNDAY))

        then: "system returns 403"
            response.expectError() == 403
    }
}