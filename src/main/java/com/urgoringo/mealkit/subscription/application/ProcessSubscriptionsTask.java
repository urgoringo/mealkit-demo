package com.urgoringo.mealkit.subscription.application;

import com.github.kagkarlsson.scheduler.task.helper.RecurringTask;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import com.github.kagkarlsson.scheduler.task.schedule.Schedules;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;

@NullMarked
@Configuration
@RequiredArgsConstructor
public class ProcessSubscriptionsTask {

    private final ProcessSubscriptionOrdersService processSubscriptionOrdersService;

    public static final String TASK_NAME = "lock-orders-task";

    @Bean
    public RecurringTask<Void> recurringLockOrdersTask() {
        return Tasks
            .recurring(TASK_NAME, Schedules.daily(LocalTime.of(0, 0)))
            .execute((_, _) ->
                processSubscriptionOrdersService.execute()
            );
    }
}
