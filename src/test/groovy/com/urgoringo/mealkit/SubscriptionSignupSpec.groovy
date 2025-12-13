package com.urgoringo.mealkit


import java.time.DayOfWeek
import java.time.LocalDate

import static com.urgoringo.mealkit.scaffolding.TestFactory.aSubscription

class SubscriptionSignupSpec extends ApplicationSpecification {

    def "subscription with 3 recipes"() {
        given: "new customer with no existing subscription"
            def authToken = app.signupCustomer().expectSuccess().token()

        when: "customer chooses 3 recipes for upcoming order"
            def chosenRecipeIds = app.getRecipes(3)
            def response = app.create(authToken, aSubscription().withRecipeIds(chosenRecipeIds))
            def subscription = response.expectSuccess()

        then: "system creates new subscription with upcoming order that contains these 3 recipes"
            subscription.id() != null
            subscription.customerId() != null
            subscription.upcomingOrders().size() == 1

            def firstOrder = subscription.upcomingOrders().get(0)
            firstOrder.recipeIds() == chosenRecipeIds
    }

    def "cannot create subscription with less than 3 recipes"() {
        given: "new customer has selected only 2 recipes"
            def authToken = app.signupCustomer().expectSuccess().token()

//            def availableRecipes = []
//            IntStream.rangeClosed(1, 2)
//                    .mapToObj(i -> app.havingRecipe("Recipe " + i))
//                    .forEach(recipe -> availableRecipes.add(recipe))
//
            def chosenRecipeIds = app.getRecipes(2)

        when: "customer tries to sign up for subscription"
            def response = app.create(authToken, aSubscription().withRecipeIds(chosenRecipeIds))

        then: "system returns 422 with validation error"
            def statusCode = response.expectError()
            statusCode == 422
    }

    def "delivery address is required"() {
        when: "customer tries to create subscription without delivery address"
            def authToken = app.signupCustomer().expectSuccess().token()
            def recipeIds = app.getRecipes(3)
            def response = app.create(authToken, aSubscription().withRecipeIds(recipeIds).withDeliveryAddress(null))

        then: "system returns 422 with validation error"
            def statusCode = response.expectError()
            statusCode == 422
    }

    def "subscription has delivery address"() {
        given: "customer home address is: Pikk 15, 10123 Tallinn, Estonia"
            def homeAddress = "Pikk 15, 10123 Tallinn, Estonia"
            def authToken = app.signupCustomer().expectSuccess().token()

        when: "they signup for subscription"
            def chosenRecipeIds = app.getRecipes(3)
            def request = aSubscription().withRecipeIds(chosenRecipeIds).withDeliveryAddress(homeAddress)
            def response = app.create(authToken, request)
            def subscription = response.expectSuccess()

        then: "subscription has customer's home address as delivery address"
            subscription != null
            subscription.deliveryAddress() != null
            subscription.deliveryAddress() == homeAddress
    }

    def "subscription delivery day determines first order deliver date"() {
        given: "today is 2025.11.19"
            def today = LocalDate.of(2025, 11, 19)
            app.freezeTimeOn(today)

        and: "customer selects Monday as the delivery day"
            def deliveryDay = DayOfWeek.MONDAY
            def authToken = app.signupCustomer().expectSuccess().token()

        when: "they signup for subscription"
            def chosenRecipeIds = app.getRecipes(3)
            def request = aSubscription().withRecipeIds(chosenRecipeIds).withDeliveryDay(deliveryDay)
            def response = app.create(authToken, request)
            def subscription = response.expectSuccess()

        then: "first order will be delivered on 2025.11.24"
            def expectedDeliveryDate = LocalDate.of(2025, 11, 24)
            subscription != null
            subscription.upcomingOrders() != null
            subscription.upcomingOrders().size() == 1

            def firstOrder = subscription.upcomingOrders().first
            firstOrder.deliveryDate() != null
            firstOrder.deliveryDate() == expectedDeliveryDate
    }
}
