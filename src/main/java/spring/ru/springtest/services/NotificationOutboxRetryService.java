package spring.ru.springtest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import spring.ru.springtest.client.NotificationKafkaClient;
import spring.ru.springtest.config.NotificationOutboxRetryProperties;
import spring.ru.springtest.exceptions.NotificationPublishException;
import spring.ru.springtest.exceptions.NotificationPublishInterruptedException;
import spring.ru.springtest.models.NotificationOutboxModel;
import spring.ru.springtest.models.enums.NotificationOutboxStatus;
import spring.ru.springtest.models.enums.NotificationStatus;
import spring.ru.springtest.repositories.NotificationOutboxRepository;
import spring.ru.springtest.repositories.NotificationRepository;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationOutboxRetryService {

    private final NotificationOutboxRepository notificationOutboxRepository;
    private final NotificationRepository notificationRepository;
    private final TransactionTemplate transactionTemplate;
    private final NotificationOutboxRetryProperties retryProperties;
    private final NotificationKafkaClient notificationKafkaClient;

    @Value("${notification-outbox.retry.max-backoff-shift}")
    private int maxBackoffShift;

    public List<NotificationOutboxModel> claimBatch() {

        OffsetDateTime now = OffsetDateTime.now();

        NotificationOutboxRetryProperties.Scheduler settings = retryProperties.scheduler();

        List<NotificationOutboxModel> claimed = transactionTemplate.execute(status ->
                notificationOutboxRepository.claimBatch(now, now.plus(settings.lease()), settings.batchSize()));

        return claimed == null ? List.of() : claimed;
    }

    public void process(NotificationOutboxModel outboxModel) {
        try {
            notificationKafkaClient.send(outboxModel.getNotificationId(), outboxModel.getPayload());
        } catch (NotificationPublishInterruptedException e) {
            log.warn("Sending of outbox entry {} was interrupted, leaving it until lease expires",
                    outboxModel.getId());
            return;
        } catch (NotificationPublishException e) {
            recordFailure(outboxModel, e);
            return;
        }
        complete(outboxModel);
    }

    private void complete(NotificationOutboxModel outboxModel) {

        boolean updated = updateIfStillOwner(outboxModel, current -> {
            notificationRepository.findById(current.getNotificationId())
                    .ifPresent(notification ->
                            notification.setNotificationStatus(NotificationStatus.SENT));
            notificationOutboxRepository.delete(current);
        });

        if (updated) {
            log.info("Notification {} published to Kafka, outbox entry {} removed",
                    outboxModel.getNotificationId(), outboxModel.getId());
        }
    }

    private void recordFailure(NotificationOutboxModel outboxModel, Exception cause) {

        int attempts = outboxModel.getAttempts() + 1;
        int maxAttempts = retryProperties.maxAttempts();
        boolean exhausted = attempts >= maxAttempts;
        OffsetDateTime nextRetryAt = exhausted ? null : calculateNextRetryAt(attempts);

        boolean updated = updateIfStillOwner(outboxModel, current -> {
            current.setAttempts(attempts);
            current.setLockedUntil(null);
            current.setNextRetryAt(nextRetryAt);
            if (exhausted) {
                current.setNotificationOutboxStatus(NotificationOutboxStatus.FAILED);
                notificationRepository.findById(current.getNotificationId())
                        .ifPresent(n -> n.setNotificationStatus(NotificationStatus.FAILED));
            }
        });

        if (!updated) {
            return;
        }

        if (exhausted) {
            log.error("Outbox entry {} permanently failed after {} attempts",
                    outboxModel.getId(), attempts, cause);
        } else {
            log.warn("Outbox entry {} failed (attempt {}/{}), next retry at {}: {}",
                    outboxModel.getId(), attempts, maxAttempts, nextRetryAt, cause.toString());
        }

    }

    private boolean updateIfStillOwner(NotificationOutboxModel claimedEntry,
                                       Consumer<NotificationOutboxModel> action) {
        try {
            return Boolean.TRUE.equals(transactionTemplate.execute(status -> {
                NotificationOutboxModel current =
                        notificationOutboxRepository.findById(claimedEntry.getId()).orElse(null);

                if (current == null || current.getVersion() != claimedEntry.getVersion()) {
                    log.warn("Outbox entry {} was taken over by another worker, skipping update",
                            claimedEntry.getId());
                    return false;
                }

                action.accept(current);
                return true;
            }));
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("Outbox entry {} was modified concurrently, skipping update", claimedEntry.getId());
            return false;
        }
    }

    private OffsetDateTime calculateNextRetryAt(int attempts) {

        int shift = Math.min(attempts - 1, maxBackoffShift);
        Duration delay = retryProperties.baseDelay().multipliedBy(1L << shift);
        Duration maxDelay = retryProperties.maxDelay();
        return OffsetDateTime.now().plus(delay.compareTo(maxDelay) > 0 ? maxDelay : delay);
    }
}
