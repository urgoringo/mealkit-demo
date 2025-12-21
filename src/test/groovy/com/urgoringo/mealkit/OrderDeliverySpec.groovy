package com.urgoringo.mealkit

class OrderDeliverySpec extends ApplicationSpecification {
    def "when upcoming order is delivered it should be removed from subscription"() {
        given: "a subscription with an upcoming order"
            def customer = app.havingCustomer()
            def subscriptionResponse = customer.havingSubscription().get()
            def nextOrder = subscriptionResponse.upcomingOrders().first

        when: "next upcoming order is delivered"
            app.deliverOrder(nextOrder.id())

        then: "subscription should not contain that order"
            def subscription = app.getCustomerSubscription(customer.authToken).expectSuccess()
            !subscription.upcomingOrders().contains(nextOrder)
    }
}