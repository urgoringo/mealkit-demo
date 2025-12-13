package com.urgoringo.mealkit


import static java.time.DayOfWeek.WEDNESDAY

class UpdateOrderRecipesSpec extends ApplicationSpecification {

    def "update recipes for upcoming order"() {
        given: "a subscription exists with 3 recipes for upcoming order"
            app
                    .havingCustomer()
                    .havingSubscription(WEDNESDAY)
            def authToken = app.currentAuthToken

            def currentSubscription = app.getCustomerSubscription(authToken).expectSuccess()
            def currentRecipeIds = currentSubscription.upcomingOrders()[0].recipeIds()

        when: "customer selects new recipes for upcoming order"
            def allRecipeIds = app.getAllRecipes()*.id()
            def newRecipeIds = (allRecipeIds - currentRecipeIds).take(3)
            app.updateUpcomingOrderRecipes(authToken, newRecipeIds)
            def subscription = app.getCustomerSubscription(authToken).expectSuccess()

        then: "system updates recipes for upcoming order"
            subscription.upcomingOrders().size() == 1

            def firstOrder = subscription.upcomingOrders().first
            firstOrder.recipeIds() == newRecipeIds
    }
}