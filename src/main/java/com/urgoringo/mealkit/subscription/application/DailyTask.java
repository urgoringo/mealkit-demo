package com.urgoringo.mealkit.subscription.application;

import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.moments.DayHasPassed;

@NullMarked
public record DailyTask(DayHasPassed event) {
}
