package com.urgoringo.mealkit.mealkit69.mealkit2

import com.urgoringo.mealkit.ApplicationSpecification

import java.time.LocalDate

class SubscriptionHistorySpec extends ApplicationSpecification {

    def "customer can see subscription history"() {
        given: "customer has a subscription with 3 delivered orders"
            app.freezeTimeOn(LocalDate.parse("2125-11-18"))
            def customer = app.havingCustomer()
            def subscription = customer.havingSubscription()
            def order1Id = subscription.get().upcomingOrders()[0].id()
            subscription.withNextUpcomingOrder().delivered()
            def order2Id = subscription.get().upcomingOrders()[0].id()
            subscription.withNextUpcomingOrder().delivered()
            def order3Id = subscription.get().upcomingOrders()[0].id()
            subscription.withNextUpcomingOrder().delivered()

        when: "customer queries subscription history"
            def response = app.getSubscriptionHistory(customer.authToken)

        then: "system returns these 3 delivered orders"
            def history = response.expectSuccess()
            history.size() == 3
            history*.id().containsAll([order1Id, order2Id, order3Id])
    }
}

