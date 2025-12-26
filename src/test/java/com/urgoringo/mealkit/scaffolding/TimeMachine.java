package com.urgoringo.mealkit.scaffolding;

import com.github.kagkarlsson.scheduler.Scheduler;
import com.github.kagkarlsson.scheduler.testhelper.SettableClock;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static com.urgoringo.mealkit.jooq.Tables.SCHEDULED_TASKS;
import static org.awaitility.Awaitility.await;

@AllArgsConstructor
@Component
public class TimeMachine {
    private final TestClock testClock;
    private final SettableClock settableClock;
    private final Scheduler scheduler;
    private final DSLContext dslContext;

    public void shiftTime(Duration duration) {
        testClock.frozenAt(testClock.instant().plus(duration));
        settableClock.set(testClock.instant().plus(duration));
        scheduler.triggerCheckForDueExecutions();
        await()
            .pollInterval(Duration.ofMillis(10))
            .atMost(Duration.ofSeconds(5))
            .until(() -> countDueTasks() == 0);
    }

    public void shiftTimeTo(LocalDate date) {
        testClock.frozenOn(date);
        settableClock.set(testClock.instant());
        scheduler.triggerCheckForDueExecutions();
        await()
            .pollInterval(Duration.ofMillis(10))
            .atMost(Duration.ofSeconds(5))
            .until(() -> countDueTasks() == 0);
    }

    public void reset() {
        testClock.reset();
        settableClock.set(testClock.instant());
    }

    private int countDueTasks() {
        return dslContext.fetchCount(dslContext.select()
            .from(SCHEDULED_TASKS)
            .where(SCHEDULED_TASKS.EXECUTION_TIME.lessOrEqual(testClock.instant().atOffset(ZoneOffset.UTC)))
            .and(SCHEDULED_TASKS.PICKED.eq(false)));
    }
}


