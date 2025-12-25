package com.urgoringo.mealkit.scaffolding;

import com.github.kagkarlsson.scheduler.Clock;
import com.github.kagkarlsson.scheduler.Scheduler;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

public class TestTimeProvider implements Clock {
    
    private final TestClock testClock;
    @Nullable
    private Scheduler scheduler;

    public TestTimeProvider(TestClock testClock, @Nullable Scheduler scheduler) {
        this.testClock = testClock;
        this.scheduler = scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public Instant now() {
        return testClock.instant();
    }

    public void triggerSchedulerCheck() {
        if (scheduler != null) {
            scheduler.triggerCheckForDueExecutions();
        }
    }
}
