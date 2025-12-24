package com.urgoringo.mealkit


import java.time.LocalDate

import static java.time.DayOfWeek.WEDNESDAY

class UpdatePendingOrderRecipesSpec extends ApplicationSpecification {

    def "update recipes for upcoming order"() {
        given: "a subscription exists with 3 recipes for upcoming order"
            app.freezeTimeOn(LocalDate.parse("2025-12-10"))
            app
                    .havingCustomer()
                    .havingSubscription(WEDNESDAY)
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
}