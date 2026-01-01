package com.urgoringo.mealkit.subscription.application;

import com.github.kagkarlsson.scheduler.SchedulerClient;
import com.github.kagkarlsson.scheduler.task.TaskInstance;
import com.urgoringo.mealkit.subscription.domain.Subscription;
import com.urgoringo.mealkit.subscription.domain.SubscriptionProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Clock;
import java.time.LocalDate;

@NullMarked
@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionTaskScheduler {

    private final SchedulerClient schedulerClient;
    private final Clock clock;

    public void scheduleProcessing(Subscription subscription) {
        String subscriptionId = subscription.id().value().toString();
        LocalDate processingDate = subscription.nextProcessingDate(clock);

        log.info("Scheduling task to process subscription {} at {}", subscriptionId, processingDate);
        TaskInstance<String> taskInstanceObj = new TaskInstance<>(
            ProcessSubscriptionsTask.TASK_NAME,
            subscriptionId + "_" + processingDate,
            subscriptionId
        );
        schedulerClient.scheduleIfNotExists(taskInstanceObj, processingDate.atStartOfDay(clock.getZone()).toInstant());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSubscriptionProcessed(SubscriptionProcessedEvent event) {
        log.info("Received event for subscription {}", event.subscription().id().value());
        scheduleProcessing(event.subscription());
    }
}
