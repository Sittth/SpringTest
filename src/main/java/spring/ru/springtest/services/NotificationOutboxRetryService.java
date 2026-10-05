package spring.ru.springtest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import spring.ru.springtest.config.NotificationOutboxRetryProperties;
import spring.ru.springtest.models.NotificationOutboxModel;
import spring.ru.springtest.models.enums.NotificationOutboxStatus;
import spring.ru.springtest.models.enums.NotificationStatus;
import spring.ru.springtest.repositories.NotificationOutboxRepository;
import spring.ru.springtest.repositories.NotificationRepository;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationOutboxRetryService {

    private static final int MAX_BACKOFF_SHIFT = 20;
    private static final long SEND_TIMEOUT_SECONDS = 10;

    private final NotificationOutboxRepository notificationOutboxRepository;
    private final NotificationRepository notificationRepository;
    private final TransactionTemplate transactionTemplate;
    private final NotificationOutboxRetryProperties retryProperties;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.notification-topic")
    private String topic;

    public List<NotificationOutboxModel> claimBatch() {

        OffsetDateTime now = OffsetDateTime.now();

        NotificationOutboxRetryProperties.Scheduler settings = retryProperties.scheduler();

        List<NotificationOutboxModel> claimed = transactionTemplate.execute(status ->
                notificationOutboxRepository.claimBatch(now, now.plus(settings.lease()), settings.batchSize()));

        return claimed == null ? List.of() : claimed;
    }

    public void process(NotificationOutboxModel outboxModel) {
        try {
            kafkaTemplate
                    .send(topic, outboxModel.getNotificationId().toString(), outboxModel.getPayload())
                    .get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            recordFailure(outboxModel, e);
            return;
        } catch (Exception e) {
            recordFailure(outboxModel, e);
            return;
        }

        complete(outboxModel);
    }

    private void complete(NotificationOutboxModel outboxModel) {

        transactionTemplate.executeWithoutResult(status -> {
            notificationRepository.findById(outboxModel.getNotificationId())
                    .ifPresent(n -> n.setNotificationStatus(NotificationStatus.SENT));
            notificationOutboxRepository.deleteById(outboxModel.getId());
        });

        log.info("Notification {} published to Kafka, outbox entry {} removed",
                outboxModel.getNotificationId(), outboxModel.getId());
    }

    private void recordFailure(NotificationOutboxModel outboxModel, Exception cause) {

        int attempts = outboxModel.getAttempts() + 1;
        int maxAttempts = retryProperties.maxAttempts();

        outboxModel.setAttempts(attempts);
        outboxModel.setLockedUntil(null);

        boolean exhausted = attempts >= maxAttempts;

        if (exhausted) {
            outboxModel.setNotificationOutboxStatus(NotificationOutboxStatus.FAILED);
            outboxModel.setNextRetryAt(null);
            log.error("Outbox entry {} permanently failed after {} attempts",
                    outboxModel.getId(), attempts, cause);
        } else {
            outboxModel.setNextRetryAt(calculateNextRetryAt(attempts));
            log.warn("Outbox entry {} failed (attempt {}/{}), next retry at {}: {}",
                    outboxModel.getId(), attempts, maxAttempts, outboxModel.getNextRetryAt(), cause.toString());
        }

        transactionTemplate.executeWithoutResult(status -> {
            notificationOutboxRepository.save(outboxModel);
            if (exhausted) {
                notificationRepository.findById(outboxModel.getNotificationId())
                        .ifPresent(n -> n.setNotificationStatus(NotificationStatus.FAILED));
            }
        });
    }

    private OffsetDateTime calculateNextRetryAt(int attempts) {
        int shift = Math.min(attempts - 1, MAX_BACKOFF_SHIFT);
        Duration delay = retryProperties.baseDelay().multipliedBy(1L << shift);
        Duration maxDelay = retryProperties.maxDelay();
        return OffsetDateTime.now().plus(delay.compareTo(maxDelay) > 0 ? maxDelay : delay);
    }
}
