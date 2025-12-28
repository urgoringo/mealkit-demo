package com.urgoringo.mealkit.subscription.application;

import com.github.kagkarlsson.scheduler.task.ExecutionContext;
import com.github.kagkarlsson.scheduler.task.helper.OneTimeTask;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@NullMarked
@Configuration
@RequiredArgsConstructor
public class ProcessSubscriptionsTask {

    private final ProcessSubscriptionOrdersService processSubscriptionOrdersService;

    public static final String TASK_NAME = "process-subscription-task";

    @Bean
    public OneTimeTask<Long> processSubscriptionTask() {
        return Tasks
            .oneTime(TASK_NAME, Long.class)
            .execute((taskInstance, _) -> {
                Long subscriptionId = taskInstance.getData();
                processSubscriptionOrdersService.execute(subscriptionId);
            });
    }
}
