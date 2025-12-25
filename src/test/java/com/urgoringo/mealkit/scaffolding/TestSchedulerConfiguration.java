package com.urgoringo.mealkit.scaffolding;

import com.github.kagkarlsson.scheduler.Scheduler;
import com.github.kagkarlsson.scheduler.SchedulerBuilder;
import com.github.kagkarlsson.scheduler.task.Task;
import com.urgoringo.mealkit.subscription.application.ProcessSubscriptionOrdersService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.List;

@TestConfiguration
public class TestSchedulerConfiguration {

    @Autowired(required = false)
    private TestTimeProvider timeProvider;

    @Autowired(required = false)
    private Scheduler scheduler;

    @Autowired(required = false)
    private TestClock testClock;

    @Autowired(required = false)
    private ProcessSubscriptionOrdersService processSubscriptionOrdersService;

    @PostConstruct
    public void wireScheduler() {
        if (timeProvider != null && scheduler != null && testClock != null) {
            timeProvider.setScheduler(scheduler);
            if (processSubscriptionOrdersService != null) {
                timeProvider.setProcessSubscriptionOrdersService(processSubscriptionOrdersService);
            }
            testClock.setTimeProvider(timeProvider);
        }
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "spring.profiles.active", havingValue = "test")
    public Scheduler testScheduler(
            DataSource dataSource,
            List<Task<?>> tasks
    ) {
        SchedulerBuilder builder = Scheduler.create(dataSource, tasks)
                .threads(1)
                .pollingInterval(java.time.Duration.ofSeconds(1))
                .heartbeatInterval(java.time.Duration.ofMinutes(5))
                .tableName("scheduled_tasks")
                .enableImmediateExecution();

        return builder.build();
    }
}
