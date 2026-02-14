package com.urgoringo.mealkit.payment.infra.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.domain.Money;
import com.urgoringo.mealkit.payment.domain.CustomerChargedEvent;
import com.urgoringo.mealkit.payment.domain.PaymentGateway;
import com.urgoringo.mealkit.payment.infra.PaymentFailedException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class StripePaymentGateway implements PaymentGateway {

    public StripePaymentGateway(
        @Value("${stripe.api-key:sk_test_mock}") String apiKey,
        @Value("${stripe.api-base:}") @Nullable String apiBase) {
        Stripe.overrideApiBase(apiBase);
        Stripe.apiKey = apiKey;
        log.info("Using Stripe API base: {}", apiBase);
    }

    @Override
    public void chargeCustomer(Id<Customer> customerId, Money amount) {
        log.info("Charging customer {} amount {}", customerId, amount);

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(toStripeAmount(amount))
                .setCurrency("usd")
                .putMetadata("customer_id", customerId.value().toString())
                .setConfirm(true)
                .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                        .build()
                )
                .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            log.info("Payment successful for customer {} with intent {}", customerId, paymentIntent.getId());
        } catch (StripeException e) {
            log.error("Payment failed for customer {}", customerId, e);
            throw new PaymentFailedException("Payment failed: " + e.getMessage(), e);
        }
    }

    private long toStripeAmount(Money amount) {
        return amount.amount().multiply(BigDecimal.valueOf(100)).longValue();
    }
}
