package com.urgoringo.mealkit.scaffolding;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class TestClock extends Clock {
    private Instant instant = Instant.now();
    private final ZoneId zoneId = ZoneId.of("UTC");

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

    public void freezeTime(LocalDate date) {
        this.instant = date.atStartOfDay(zoneId).toInstant();
    }

    public void reset() {
        this.instant = Instant.now();
    }


}
