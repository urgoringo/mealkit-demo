package com.urgoringo.mealkit

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus

class ExampleSpec extends ApplicationSpecification {

    @Autowired
    ApplicationContext context

    @Autowired
    TestRestTemplate restTemplate

    def "Basic application startup"() {
        given: "the Mealkit application is running"
            def applicationRunning = context != null

        when: "I check the application status"
            def statusChecked = false
            if (applicationRunning) {
                try {
                    def response = restTemplate.getForEntity("/health", String.class)
                    statusChecked = response.getStatusCode() == HttpStatus.OK
                } catch (Exception e) {
                    statusChecked = false
                }
            }

        then: "the application should be healthy"
            applicationRunning && statusChecked
    }
}
