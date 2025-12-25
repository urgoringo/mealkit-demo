package com.urgoringo.mealkit.scaffolding;

import com.github.kagkarlsson.scheduler.Scheduler;
import com.urgoringo.mealkit.subscription.application.ProcessSubscriptionOrdersService;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

public class TestTimeProvider {
    
    private final TestClock testClock;
    @Nullable
    private Scheduler scheduler;
    @Nullable
    private ProcessSubscriptionOrdersService processSubscriptionOrdersService;

    public TestTimeProvider(TestClock testClock, @Nullable Scheduler scheduler) {
        this.testClock = testClock;
        this.scheduler = scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setProcessSubscriptionOrdersService(ProcessSubscriptionOrdersService service) {
        this.processSubscriptionOrdersService = service;
    }

    public Instant now() {
        return testClock.instant();
    }

    public void triggerSchedulerCheck() {
        // Directly execute the service logic instead of trying to trigger the scheduler
        if (processSubscriptionOrdersService != null) {
            processSubscriptionOrdersService.execute();
        }
    }
}
