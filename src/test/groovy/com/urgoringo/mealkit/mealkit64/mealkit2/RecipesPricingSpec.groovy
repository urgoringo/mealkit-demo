package com.urgoringo.mealkit.mealkit64.mealkit2

import com.urgoringo.mealkit.ApplicationSpecification

import static com.urgoringo.mealkit.recipecatalog.domain.PricingCategory.*
import static com.urgoringo.mealkit.scaffolding.TestFactory.aRecipe
import static com.urgoringo.mealkit.scaffolding.TestFactory.aSubscription
import static java.time.DayOfWeek.WEDNESDAY

class RecipesPricingSpec extends ApplicationSpecification {

    def "recipe pricing is based on pricing category"() {
        given:
            "system has a recipe with pricing category MEDIUM"
            def recipeId = app.havingRecipe(aRecipe().withPricingCategory(MEDIUM)).id()

        when: "customer queries recipe"
            def recipe = app.getRecipe(recipeId)

        then: "system returns recipe MEDIUM pricing details"
            recipe.pricingCategory() == MEDIUM
            recipe.price() == 10.50
    }

    def "order has total price"() {
        given:
            "system has a recipe with pricing category LOW, MEDIUM, HIGH"
            def lowRecipeId = app.havingRecipe(aRecipe().withPricingCategory(LOW)).id()
            def mediumRecipeId = app.havingRecipe(aRecipe().withPricingCategory(MEDIUM)).id()
            def highRecipeId = app.havingRecipe(aRecipe().withPricingCategory(HIGH)).id()

        and:
            "customer has an order with LOW, MEDIUM, HIGH recipes"
            def recipeIds = [lowRecipeId, mediumRecipeId, highRecipeId]
            def customer = app.havingCustomer()
            customer.having(aSubscription().withDeliveryDay(WEDNESDAY).withRecipeIds(recipeIds))

        when: "customer queries their subscription"
            def subscription = app.getCustomerSubscription(customer.authToken).expectSuccess()

        then: "system subscription has total price"
            def order = subscription.upcomingOrders()[0]
            order.totalPrice() == 30.50
    }
}