package com.urgoringo.mealkit

import com.urgoringo.mealkit.scaffolding.ApplicationRunner
import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureEmbeddedDatabase(refresh = RefreshMode.NEVER)
@Import(EmbeddedDatabaseConfiguration)
@ActiveProfiles("test")
abstract class ApplicationSpecification extends Specification {

    @Autowired
    ApplicationRunner app
    @LocalServerPort
    int port

    def setup() {
        app.start(port)
    }

}
