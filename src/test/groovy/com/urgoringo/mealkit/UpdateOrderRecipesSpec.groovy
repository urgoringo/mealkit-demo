package com.urgoringo.mealkit


import java.time.LocalDate

class UpdateOrderRecipesSpec extends ApplicationSpecification {

    def "update recipes for upcoming order"() {
        given: "a subscription exists with 3 recipes for upcoming order"
            def signupDate = LocalDate.parse("2025-11-18")
            def deliveryDate = LocalDate.parse("2025-11-24")

            app
                    .freezeTimeOn(signupDate)
            def authToken = app
                    .having()
                    .subscription(deliveryDate.getDayOfWeek())
                    .authToken

        when: "customer updates recipes for upcoming order"
            def newRecipeIds = app.getAllRecipes().collect { recipe -> recipe.id() }.take(3)
            app.updateUpcomingOrderRecipes(authToken, newRecipeIds)
            def subscription = app.getCustomerSubscription(authToken).expectSuccess()

        then: "system updates recipes for upcoming order"
            subscription.upcomingOrders().size() == 1

            def firstOrder = subscription.upcomingOrders()[0]
            firstOrder.recipeIds() == newRecipeIds
    }
}