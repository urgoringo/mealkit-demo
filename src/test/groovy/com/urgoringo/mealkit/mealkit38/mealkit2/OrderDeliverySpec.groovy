package com.urgoringo.mealkit.mealkit38.mealkit2

import com.urgoringo.mealkit.ApplicationSpecification

import static java.time.Period.ofDays

class OrderDeliverySpec extends ApplicationSpecification {
    def "when upcoming order is delivered it should be removed from subscription"() {
        given: "a subscription with an upcoming order"
            def customer = app.havingCustomer()
            def subscriptionResponse = customer.havingSubscription().get()
            def nextOrder = subscriptionResponse.upcomingOrders().first

        and: "time is set to the delivery date"
            app.freezeTimeOn(nextOrder.deliveryDate())

        when: "next upcoming order is delivered"
            app.backoffice().markOrderDelivered(nextOrder.id())

        then: "subscription should not contain that order"
            def subscription = app.getCustomerSubscription(customer.authToken).expectSuccess()
            !subscription.upcomingOrders().contains(nextOrder)
    }

    def "customer cannot deliver their own order"() {
        given: "a customer with a subscription"
            def customer = app.havingCustomer()
            def subscriptionResponse = customer.havingSubscription().get()
            def nextOrder = subscriptionResponse.upcomingOrders().first

        and: "time is set to the delivery date"
            app.freezeTimeOn(nextOrder.deliveryDate())

        when: "customer tries to deliver their order"
            def response = app.tryDeliverOrderAsCustomer(nextOrder.id(), customer.authToken)

        then: "request is forbidden"
            response.expectError() == 403
    }

    def "cannot deliver order before delivery date"() {
        given: "a subscription with an upcoming order"
            def customer = app.havingCustomer()
            def subscriptionResponse = customer.havingSubscription().get()
            def nextOrder = subscriptionResponse.upcomingOrders().first

        and: "current date is before the delivery date"
            app.freezeTimeOn(nextOrder.deliveryDate() - ofDays(1))

        when: "backoffice tries to mark the order as delivered"
            def response = app.backoffice().tryMarkOrderDelivered(nextOrder.id())

        then: "system returns validation error"
            response.expectError() == 422
    }
}