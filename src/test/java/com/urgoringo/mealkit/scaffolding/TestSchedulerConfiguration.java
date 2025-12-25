package com.urgoringo.mealkit.scaffolding;

import com.github.kagkarlsson.scheduler.Scheduler;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
public class TestSchedulerConfiguration {

    @Autowired(required = false)
    private TestTimeProvider timeProvider;

    @Autowired(required = false)
    private Scheduler scheduler;

    @Autowired(required = false)
    private TestClock testClock;

    @PostConstruct
    public void wireScheduler() {
        if (timeProvider != null && scheduler != null && testClock != null) {
            timeProvider.setScheduler(scheduler);
            testClock.setTimeProvider(timeProvider);
        }
    }
}
