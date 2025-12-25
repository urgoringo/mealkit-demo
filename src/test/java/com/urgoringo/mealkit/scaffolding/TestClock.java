package com.urgoringo.mealkit.scaffolding;

import org.jetbrains.annotations.Nullable;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class TestClock extends Clock {
    private Instant instant = Instant.now();
    private final ZoneId zoneId = ZoneId.of("UTC");
    @Nullable
    private TestTimeProvider timeProvider;

    @Override
    public ZoneId getZone() {
        return zoneId;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        throw new UnsupportedOperationException("TestClock does not support withZone");
    }

    @Override
    public Instant instant() {
        return instant;
    }

    public void setTimeProvider(TestTimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public void frozenOn(LocalDate date) {
        this.instant = date.atStartOfDay(zoneId).toInstant();
        // Trigger scheduler to check for due executions when time changes
        if (timeProvider != null) {
            timeProvider.triggerSchedulerCheck();
        }
    }

    public void reset() {
        this.instant = Instant.now();
    }

    public LocalDate date() {
        return Instant.ofEpochSecond(instant.getEpochSecond()).atZone(zoneId).toLocalDate();
    }
}
