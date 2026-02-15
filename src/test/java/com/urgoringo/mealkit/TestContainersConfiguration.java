package com.urgoringo.mealkit;

import com.github.kagkarlsson.scheduler.testhelper.SettableClock;
import com.urgoringo.mealkit.scaffolding.TestClock;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;


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
    public static BeanPostProcessor zonkyDataSourcePoolingPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (!(bean instanceof DataSource dataSource) || !"dataSource".equals(beanName) || bean instanceof HikariDataSource) {
                    return bean;
                }

                HikariConfig config = new HikariConfig();
                config.setPoolName("zonky-test-pool");
                config.setDataSource(dataSource);
                config.setMaximumPoolSize(2);
                config.setMinimumIdle(2);
                config.setIdleTimeout(0);
                config.setMaxLifetime(0);
                config.setKeepaliveTime(0);
                config.setConnectionTimeout(30_000);

                return new HikariDataSource(config);
            }
        };
    }

    @Bean(destroyMethod = "close")
    public CloseableHttpClient pooledHttpClient() {
        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
            .setMaxConnTotal(200)
            .setMaxConnPerRoute(50)
            .build();

        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(Timeout.ofSeconds(30))
            .setConnectTimeout(Timeout.ofSeconds(30))
            .setResponseTimeout(Timeout.ofSeconds(30))
            .build();

        return HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(requestConfig)
            .evictExpiredConnections()
            .build();
    }

    @Bean
    public RestClient.Builder restClientBuilder(CloseableHttpClient pooledHttpClient) {
        HttpComponentsClientHttpRequestFactory requestFactory =
            new HttpComponentsClientHttpRequestFactory(pooledHttpClient);
        return RestClient.builder()
            .requestFactory(requestFactory)
            .defaultStatusHandler(status -> true, (request, response) -> {});
    }

}
