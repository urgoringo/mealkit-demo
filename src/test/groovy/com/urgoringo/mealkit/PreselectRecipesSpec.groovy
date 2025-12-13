package com.urgoringo.mealkit


import java.time.LocalDate

class PreselectRecipesSpec extends ApplicationSpecification {

    def "select recipes for the next upcoming order"() {
        given: "a subscription exists where upcoming order has delivery date 2025.11.24"
            def signupDate = LocalDate.parse("2025-11-18")
            def deliveryDate = LocalDate.parse("2025-11-24")

            app.freezeTimeOn(signupDate)
            app
                    .havingCustomer()
                    .havingSubscription(deliveryDate.getDayOfWeek())
            def authToken = app.currentAuthToken

        when: "current day becomes 2025.11.21"
            def currentDate = LocalDate.parse("2025-11-21")
            app.freezeTimeOn(currentDate)
                    .processSubscriptionOrders()
            def subscription = app.getCustomerSubscription(authToken).expectSuccess()

        then: "system adds new upcoming order with delivery date 2025.12.01"
            subscription.upcomingOrders().size() == 2

            def expectedDeliveryDate = LocalDate.parse("2025-12-01")
            def secondOrder = subscription.upcomingOrders().get(1)
            secondOrder.deliveryDate() == expectedDeliveryDate
    }

}
