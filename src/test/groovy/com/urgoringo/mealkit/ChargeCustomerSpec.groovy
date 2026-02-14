package com.urgoringo.mealkit

class ChargeCustomerSpec extends ApplicationSpecification {

    def "when order is locked then charge customer"() {
        given: "a subscription"
            def customer = app.havingCustomer()
            def order = customer.havingSubscription().withNextUpcomingOrder()
        
        when: "order is locked"
            def lockedOrder = order.locked().get()

        then: "system charges customer"
            app.billingSystem().customerIsCharged(lockedOrder.totalPrice())
    }
}

