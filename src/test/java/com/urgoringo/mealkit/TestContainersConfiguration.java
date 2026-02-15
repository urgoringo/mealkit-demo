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
        return new PostgreSQLContainer<>("postgres:18.1-alpine")
                .withReuse(true)
                .withCommand(
                        "postgres",
                        // Disable fsync for test speed (safe for tests, not for production)
                        "-c", "fsync=off",
                        "-c", "synchronous_commit=off",
                        "-c", "full_page_writes=off",
                        // I/O concurrency settings
                        "-c", "effective_io_concurrency=200",
                        "-c", "maintenance_io_concurrency=50",
                        // Memory and buffer settings (adjusted for container environment)
                        "-c", "shared_buffers=256MB",
                        "-c", "effective_cache_size=1GB",
                        // WAL settings for faster writes
                        "-c", "max_wal_size=2GB",
                        "-c", "checkpoint_timeout=30min",
                        // Planner settings for faster queries
                        "-c", "random_page_cost=1.1"
                );
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

