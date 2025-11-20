package com.urgoringo.mealkit.cucumber;

import com.urgoringo.mealkit.TestContainersConfiguration;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Cucumber Spring configuration for integration tests.
 * Integrates with Spring Boot Test and TestContainers for database testing.
 */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestContainersConfiguration.class)
public class CucumberTest {
}
