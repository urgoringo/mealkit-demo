package com.urgoringo.mealkit.mealkit43.mealkit2

import com.urgoringo.mealkit.ApplicationSpecification

import static com.urgoringo.mealkit.scaffolding.TestFactory.aPassword
import static com.urgoringo.mealkit.scaffolding.TestFactory.anEmail

class CustomerAuthenticationSpec extends ApplicationSpecification {

    def "new customer signup"() {
        given: "user signs up using their email and password"
            def email = anEmail()
            def password = aPassword()
            app.signupCustomer(email, password).expectSuccess()

        when: "customer logs in with their credentials"
            def loginResponse = app.loginCustomer(email, password)

        then: "system returns an access token"
            loginResponse.expectSuccess().token()
    }

    def "duplicate email"() {
        given: "customer with email: jane.doe@example.com already exists"
            def email = "jane.doe@example.com"
            app.signupCustomer(email, aPassword()).expectSuccess()

        when: "customer tries to signup using jane.doe@example.com"
            def response = app.signupCustomer(email, aPassword())

        then: "system returns 422 with validation error"
            def statusCode = response.expectError()
            statusCode == 422
    }
}
