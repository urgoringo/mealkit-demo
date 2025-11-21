package com.urgoringo.mealkit;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;


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
    public static class TestClock extends Clock {
        private Instant instant = Instant.now();
        private final ZoneId zoneId = ZoneId.of("UTC");

        @Override
        public ZoneId getZone() {
            return zoneId;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            throw new UnsupportedOperationException("TestClock does not support withZone");
        }

        @Override
        public Instant instant() {
            return instant;
        }

        public void freezeTime(LocalDate date) {
            this.instant = date.atStartOfDay(zoneId).toInstant();
        }
    }

}

