package com.urgoringo.mealkit.domain;

import org.jspecify.annotations.NullMarked;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@NullMarked
public record Money(BigDecimal amount) {
    
    public Money {
        Objects.requireNonNull(amount, "Amount cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money amount cannot be negative");
        }
        if (amount.scale() > 2) {
            amount = amount.setScale(2, RoundingMode.HALF_UP);
        }
    }
    
    public static Money of(double amount) {
        return new Money(BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP));
    }
    
    public static Money of(String amount) {
        return new Money(new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP));
    }
    
    public static Money of(BigDecimal amount) {
        return new Money(amount.setScale(2, RoundingMode.HALF_UP));
    }
    
    public double toDouble() {
        return amount.doubleValue();
    }
    
    @Override
    public String toString() {
        return amount.toString();
    }
}
