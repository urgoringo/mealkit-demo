package com.urgoringo.mealkit;

import com.urgoringo.mealkit.cucumber.scaffolding.TestClock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.PostgreSQLContainer;


@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:17-alpine")
                .withReuse(true);
    }

    @Bean
    @Primary
    public TestClock testClock() {
        return new TestClock();
    }

    /**
     * Mutable Clock implementation for testing.
     * Allows tests to control the current time by calling freezeTime().
     */

}

