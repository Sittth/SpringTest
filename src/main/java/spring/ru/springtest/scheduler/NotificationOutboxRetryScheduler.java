package spring.ru.springtest.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spring.ru.springtest.models.NotificationOutboxModel;
import spring.ru.springtest.services.NotificationOutboxRetryService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationOutboxRetryScheduler {

    private final NotificationOutboxRetryService notificationOutboxRetryService;

    @Scheduled(fixedDelayString = "${notification-outbox.retry.scheduler.fixed-delay-ms:5000}")
    public void retryNotificationOutbox() {

        List<NotificationOutboxModel> claimed = notificationOutboxRetryService.claimBatch();

        for (NotificationOutboxModel entry : claimed) {
            try {
                notificationOutboxRetryService.process(entry);
            } catch (Exception e) {
                log.error("Unexpected error while processing notification outbox entry {}", entry.getId(), e);
            }
        }
    }
}
