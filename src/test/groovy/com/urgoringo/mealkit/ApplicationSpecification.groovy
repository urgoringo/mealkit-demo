package com.urgoringo.mealkit

import com.urgoringo.mealkit.scaffolding.ApplicationRunner
import com.urgoringo.mealkit.scaffolding.SubscriptionSetup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainersConfiguration)
@ActiveProfiles("test")
abstract class ApplicationSpecification extends Specification {

    @Autowired
    ApplicationRunner app

    def setup() {
        app.setup()
    }

}
