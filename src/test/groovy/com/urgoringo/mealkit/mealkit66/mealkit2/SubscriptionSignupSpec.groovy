package com.urgoringo.mealkit.mealkit66.mealkit2

import com.urgoringo.mealkit.ApplicationSpecification
import spock.lang.Narrative

import java.time.LocalDate

import static com.urgoringo.mealkit.scaffolding.TestFactory.aSubscription
import static java.time.DayOfWeek.MONDAY

@Narrative("""
Each customer can sign up for a subscription. 
When they have an active subscription, they will get a meal kit order delivered to them each week.
One meal kit order contains 3 recipes selected by the customer and ingredients needed for those recipes.
""")
class SubscriptionSignupSpec extends ApplicationSpecification {

    def "customer can choose 3 recipes for upcoming order"() {
        given: "new customer with no existing subscription"
            def authToken = app.signupCustomer().expectSuccess().token()

        when: "customer chooses 3 recipes for upcoming order"
            def chosenRecipeIds = app.getRecipes(3)
            def response = app.create(aSubscription().withRecipeIds(chosenRecipeIds), authToken)
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

            def chosenRecipeIds = app.getRecipes(2)

        when: "customer tries to sign up for subscription"
            def response = app.create(aSubscription().withRecipeIds(chosenRecipeIds), authToken)

        then: "system returns 422 with validation error"
            def statusCode = response.expectError()
            statusCode == 422
    }

    def "cannot create subscription with more than 8 recipes"() {
        given: "new customer has selected 9 recipes"
            def authToken = app.signupCustomer().expectSuccess().token()

            def chosenRecipeIds = app.getRecipes(9)

        when: "customer tries to sign up for subscription"
            def response = app.create(aSubscription().withRecipeIds(chosenRecipeIds), authToken)

        then: "system returns 422 with validation error"
            def statusCode = response.expectError()
            statusCode == 422
    }

    def "subscription can have 8 recipes"() {
        given: "new customer has selected 8 recipes"
            def authToken = app.signupCustomer().expectSuccess().token()

            def chosenRecipeIds = app.getRecipes(8)

        when: "customer tries to sign up for subscription"
            def response = app.create(aSubscription().withRecipeIds(chosenRecipeIds), authToken)

        then: "system creates new subscription with upcoming order that contains these 8 recipes"
            def subscription = response.expectSuccess()
            subscription.upcomingOrders().first.recipeIds() == chosenRecipeIds
    }

    def "delivery address is required"() {
        when: "customer tries to create subscription without delivery address"
            def authToken = app.signupCustomer().expectSuccess().token()
            def recipeIds = app.getRecipes(3)
            def response = app.create(aSubscription().withRecipeIds(recipeIds).withDeliveryAddress(null), authToken)

        then: "system returns 422 with validation error"
            def statusCode = response.expectError()
            statusCode == 422
    }

    def "can see subscription delivery address"() {
        given: "customer home address is: Pikk 15, 10123 Tallinn, Estonia"
            def homeAddress = "Pikk 15, 10123 Tallinn, Estonia"
            def authToken = app.signupCustomer().expectSuccess().token()

        when: "they signup for subscription"
            def chosenRecipeIds = app.getRecipes(3)
            def request = aSubscription().withRecipeIds(chosenRecipeIds).withDeliveryAddress(homeAddress)
            def response = app.create(request, authToken)
            def subscription = response.expectSuccess()

        then: "subscription has customer's home address as delivery address"
            subscription != null
            subscription.deliveryAddress() != null
            subscription.deliveryAddress() == homeAddress
    }

    def "subscription delivery day determines first order deliver date"() {
        given: "today is 2025.11.19 (Wednesday)"
            def today = LocalDate.of(2025, 11, 19)
            app.freezeTimeOn(today)
            def customer = app.havingCustomer()

        when: "customer creates subscription they select Monday as the delivery day"
            def deliveryDay = MONDAY
            customer.having(deliveryDay)

        then: "first order will be delivered on 2025.11.24 (next Monday)"
            def subscription = app.getCustomerSubscription(customer.authToken).expectSuccess()
            def expectedDeliveryDate = LocalDate.of(2025, 11, 24)
            subscription != null
            subscription.upcomingOrders() != null
            subscription.upcomingOrders().size() == 1

            def firstOrder = subscription.upcomingOrders().first
            firstOrder.deliveryDate() != null
            firstOrder.deliveryDate() == expectedDeliveryDate
    }
}
