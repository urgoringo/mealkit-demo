package com.urgoringo.mealkit.scaffolding;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class BillingSystemDouble {

    private final List<BigDecimal> chargedAmounts = new ArrayList<>();

    public void recordCharge(BigDecimal amount) {
        chargedAmounts.add(amount);
    }

    public void customerIsCharged(BigDecimal expectedAmount) {
        if (!chargedAmounts.contains(expectedAmount)) {
            throw new AssertionError(
                "Expected customer to be charged " + expectedAmount + 
                " but charges were: " + chargedAmounts
            );
        }
    }

    public void reset() {
        chargedAmounts.clear();
    }
}
