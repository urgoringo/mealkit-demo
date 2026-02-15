package com.urgoringo.mealkit.mealkit39.mealkit2

import com.urgoringo.mealkit.ApplicationSpecification

import java.time.LocalDate

import static java.time.DayOfWeek.*

class UpdatePendingOrderRecipesSpec extends ApplicationSpecification {

    def "update recipes for upcoming order"() {
        given: "a subscription exists with 3 recipes for upcoming order"
            app.freezeTimeOn(LocalDate.parse("2025-12-10"))
            app
                .havingCustomer()
                .having(WEDNESDAY)
            def authToken = app.currentAuthToken

            def currentSubscription = app.getCustomerSubscription(authToken).expectSuccess()
            def currentRecipeIds = currentSubscription.upcomingOrders()[0].recipeIds()

        when: "customer selects new recipes for upcoming order"
            def allRecipeIds = app.getAllRecipes()*.id()
            def newRecipeIds = (allRecipeIds - currentRecipeIds).take(3)
            def orderId = currentSubscription.upcomingOrders()[0].id()
            app.updateUpcomingOrderRecipes(orderId, newRecipeIds, authToken).expectSuccess()
            def subscription = app.getCustomerSubscription(authToken).expectSuccess()

        then: "system updates recipes for upcoming order"
            subscription.upcomingOrders().size() == 1

            def firstOrder = subscription.upcomingOrders().first
            firstOrder.recipeIds() == newRecipeIds
    }

    def "update delivery day for upcoming order"() {
        given: "customer has a subscription with delivery day Monday (2025.11.24)"
            app.freezeTimeOn(LocalDate.of(2025, 11, 19))
            def customer = app.havingCustomer()
            customer.having(MONDAY)
            def subscription = app.getCustomerSubscription(customer.authToken).expectSuccess()
            def orderId = subscription.upcomingOrders().first.id()

        when: "customer updates delivery day of only the upcoming order to Tuesday"
            def response = app.updateUpcomingOrderDeliveryDay(orderId, TUESDAY, customer.authToken)
            def updatedSubscription = response.expectSuccess()

        then: "upcoming order delivery day is Tuesday (2025.11.25)"
            def firstOrder = updatedSubscription.upcomingOrders().first
            firstOrder.deliveryDate() == LocalDate.of(2025, 11, 25)

        and: "subscription delivery day is still Monday"
            updatedSubscription.deliveryDay() == MONDAY
    }
}