package com.urgoringo.mealkit;

import com.github.kagkarlsson.scheduler.testhelper.SettableClock;
import com.urgoringo.mealkit.scaffolding.TestClock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;
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

    @Bean
    public SettableClock dbSchedulerClock() {
        return new SettableClock();
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder()
                .defaultStatusHandler(status -> true, (request, response) -> {});
    }

}

