package com.urgoringo.mealkit;

import com.github.kagkarlsson.scheduler.testhelper.SettableClock;
import com.urgoringo.mealkit.payment.infra.StripeMockConfiguration;
import com.urgoringo.mealkit.payment.infra.StripeTestConfiguration;
import com.urgoringo.mealkit.scaffolding.BillingSystemDouble;
import com.urgoringo.mealkit.scaffolding.CustomerChargedEventListener;
import com.urgoringo.mealkit.scaffolding.TestClock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@TestConfiguration(proxyBeanMethods = false)
@Import({StripeMockConfiguration.class, StripeTestConfiguration.class})
public class EmbeddedDatabaseConfiguration {

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

    @Bean
    public BillingSystemDouble billingSystemDouble() {
        return new BillingSystemDouble();
    }

    @Bean
    public CustomerChargedEventListener customerChargedEventListener(BillingSystemDouble billingSystemDouble) {
        return new CustomerChargedEventListener(billingSystemDouble);
    }

}
