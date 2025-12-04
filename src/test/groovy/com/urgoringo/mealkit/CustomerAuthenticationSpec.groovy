package com.urgoringo.mealkit

import com.urgoringo.mealkit.cucumber.ApplicationRunner
import org.springframework.beans.factory.annotation.Autowired

import static com.urgoringo.mealkit.cucumber.ApplicationRunner.SubscriptionRequest.aSubscription
import static com.urgoringo.mealkit.cucumber.scaffolding.TestFactory.aPassword
import static com.urgoringo.mealkit.cucumber.scaffolding.TestFactory.anEmail

class CustomerAuthenticationSpec extends ApplicationSpecification {

    @Autowired
    ApplicationRunner app

    def setup() {
        app.deleteAllSubscriptions()
        app.deleteAllRecipes()
    }

    def "new customer signup"() {
        when: "user signs up using their email and password"
            def email = anEmail()
            def password = aPassword()
            def signupResponse = app.signupCustomer(email, password).expectSuccess()

        then: "system creates new customer with used credentials"
            signupResponse.id() != null
            signupResponse.email() == email
    }

    def "customer login"() {
        given: "customer with email john.doe@example.com exists"
            def email = "john.doe@example.com"
            def password = aPassword()
            def customer = app.signupCustomer(email, password).expectSuccess()
            def customerId = customer.id()

        and: "has a subscription"
            def recipeIds = app.havingRecipes(3)
            app.create(customer.token(), aSubscription(recipeIds)).expectSuccess()

        when: "they log in using their email and password"
            def loginResponse = app.loginCustomer(email, password)
            def accessToken = loginResponse.expectSuccess().token()

        then: "they can access their subscription"
            def subscription = app.getCustomerSubscription(accessToken).expectSuccess()
            subscription != null
            subscription.id() != null
            subscription.customerId() != null
            subscription.customerId() == customerId
    }

    def "duplicate email"() {
        given: "customer with email: jane.doe@example.com already exists"
            def email = "jane.doe@example.com"
            def password = aPassword()
            app.signupCustomer(email, password).expectSuccess()

        when: "customer tries to signup using jane.doe@example.com"
            def response = app.signupCustomer(email, aPassword())

        then: "system returns 422 with validation error"
            def statusCode = response.expectError()
            statusCode == 422
    }
}
