package com.urgoringo.mealkit.payment.infra;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class StripeMockConfiguration {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public StripeMockContainer stripeMockContainer() {
        return new StripeMockContainer();
    }

    @Getter
    public static class StripeMockContainer extends GenericContainer<StripeMockContainer> {
        private static final int STRIPE_MOCK_PORT = 12111;

        public StripeMockContainer() {
            super(DockerImageName.parse("stripe/stripe-mock:v0.186.0"));
            withExposedPorts(STRIPE_MOCK_PORT);
            withReuse(true);
        }

        public String getApiBase() {
            return "http://" + getHost() + ":" + getMappedPort(STRIPE_MOCK_PORT);
        }
    }
}
