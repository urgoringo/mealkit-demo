package com.urgoringo.mealkit;

import com.urgoringo.mealkit.scaffolding.TestClock;
import com.urgoringo.mealkit.scaffolding.TestSchedulerConfiguration;
import com.urgoringo.mealkit.scaffolding.TestTimeProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;


@TestConfiguration(proxyBeanMethods = false)
@Import(TestSchedulerConfiguration.class)
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
    public TestTimeProvider testTimeProvider(TestClock testClock) {
        return new TestTimeProvider(testClock, null);
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder()
                .defaultStatusHandler(status -> true, (request, response) -> {});
    }

}

