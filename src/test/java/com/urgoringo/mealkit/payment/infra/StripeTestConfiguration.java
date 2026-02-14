package com.urgoringo.mealkit.payment.infra;

import com.stripe.Stripe;
import com.urgoringo.mealkit.payment.infra.StripeMockConfiguration.StripeMockContainer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
@RequiredArgsConstructor
@Slf4j
public class StripeTestConfiguration {

    private final StripeMockContainer stripeMockContainer;

    @PostConstruct
    public void configureStripe() {
        String apiBase = stripeMockContainer.getApiBase();
        log.info("Configuring Stripe API base to: {}", apiBase);
        Stripe.overrideApiBase(apiBase);
        Stripe.apiKey = "sk_test_mock";
    }
}
