package com.urgoringo.mealkit

import com.urgoringo.mealkit.cucumber.ApplicationRunner
import org.springframework.beans.factory.annotation.Autowired

import java.time.DayOfWeek
import java.time.LocalDate
import java.util.stream.IntStream

import static com.urgoringo.mealkit.cucumber.ApplicationRunner.SubscriptionRequest.aSubscription
import static com.urgoringo.mealkit.cucumber.scaffolding.TestFactory.aCustomerEmail
import static com.urgoringo.mealkit.cucumber.scaffolding.TestFactory.aPassword

class SubscriptionSignupSpec extends ApplicationSpecification {

    @Autowired
    ApplicationRunner app

    def setup() {
        app.deleteAllSubscriptions()
        app.deleteAllRecipes()
        app.reset()
    }

    def "happy path"() {
        given: "customer has no existing subscription"
            def customerEmail = aCustomerEmail()
            def customerPassword = aPassword()
            def authToken = app.signupCustomer(customerEmail, customerPassword).expectSuccess().token()

        and: "3 recipes are available in the system"
            def availableRecipes = []
            IntStream.rangeClosed(1, 3)
                    .mapToObj(i -> app.havingRecipe("Recipe " + i))
                    .forEach(recipe -> availableRecipes.add(recipe))

        when: "customer chooses these recipes for upcoming order"
            def chosenRecipeIds = availableRecipes.stream()
                    .map(recipe -> recipe.id())
                    .toList()
            def response = app.create(authToken, aSubscription(chosenRecipeIds))
            def subscription = response.expectSuccess()

        then: "system creates new subscription with upcoming order that contains these 3 recipes"
            subscription != null
            subscription.id() != null
            subscription.customerId() != null
            subscription.upcomingOrders() != null
            subscription.upcomingOrders().size() == 1

            def firstOrder = subscription.upcomingOrders().get(0)
            firstOrder.recipeIds() != null
            firstOrder.recipeIds().size() == 3
            firstOrder.recipeIds() == chosenRecipeIds
    }

    def "must select at least 3 recipes"() {
        given: "customer has selected only 2 recipes"
            def customerEmail = aCustomerEmail()
            def customerPassword = aPassword()
            def authToken = app.signupCustomer(customerEmail, customerPassword).expectSuccess().token()

            def availableRecipes = []
            IntStream.rangeClosed(1, 2)
                    .mapToObj(i -> app.havingRecipe("Recipe " + i))
                    .forEach(recipe -> availableRecipes.add(recipe))

            def chosenRecipeIds = availableRecipes.stream()
                    .map(recipe -> recipe.id())
                    .toList()

        when: "customer tries to sign up for subscription"
            def response = app.create(authToken, aSubscription(chosenRecipeIds))

        then: "system returns 422 with validation error"
            def statusCode = response.expectError()
            statusCode == 422
    }

    def "delivery address is required"() {
        when: "customer tries to signup without delivery address"
            def customerEmail = aCustomerEmail()
            def customerPassword = aPassword()
            def authToken = app.signupCustomer(customerEmail, customerPassword).expectSuccess().token()
            def recipeIds = app.havingRecipes(3)
            def response = app.create(authToken, aSubscription(recipeIds).withDeliveryAddress(null))

        then: "system returns 422 with validation error"
            def statusCode = response.expectError()
            statusCode == 422
    }

    def "subscription has delivery address"() {
        given: "customer home address is:"
            """
        Pikk 15
        10123 Tallinn
        Estonia
        """
            def homeAddress = """
                Pikk 15
                10123 Tallinn
                Estonia
            """
            def customerEmail = aCustomerEmail()
            def customerPassword = aPassword()
            def authToken = app.signupCustomer(customerEmail, customerPassword).expectSuccess().token()

        when: "they signup for subscription"
            def chosenRecipeIds = app.havingRecipes(3)
            def request = aSubscription(chosenRecipeIds).withDeliveryAddress(homeAddress)
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
            def customerEmail = aCustomerEmail()
            def customerPassword = aPassword()
            def authToken = app.signupCustomer(customerEmail, customerPassword).expectSuccess().token()

        when: "they signup for subscription"
            def chosenRecipeIds = app.havingRecipes(3)
            def request = aSubscription(chosenRecipeIds).withDeliveryDay(deliveryDay)
            def response = app.create(authToken, request)
            def subscription = response.expectSuccess()

        then: "first order will be delivered on 2025.11.24"
            def expectedDeliveryDate = LocalDate.of(2025, 11, 24)
            subscription != null
            subscription.upcomingOrders() != null
            subscription.upcomingOrders().size() == 1

            def firstOrder = subscription.upcomingOrders().get(0)
            firstOrder.deliveryDate() != null
            firstOrder.deliveryDate() == expectedDeliveryDate
    }
}
