package spring.ru.springtest.scheduler;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import spring.ru.springtest.controller.AbstractControllerTest;
import spring.ru.springtest.dto.create.NotificationCreateRequest;
import spring.ru.springtest.dto.response.NotificationResponse;
import spring.ru.springtest.models.NotificationModel;
import spring.ru.springtest.models.enums.NotificationStatus;
import spring.ru.springtest.repositories.NotificationOutboxRepository;
import spring.ru.springtest.repositories.NotificationRepository;
import spring.ru.springtest.services.NotificationService;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationOutboxRetrySchedulerTest extends AbstractControllerTest {

    @Autowired
    NotificationOutboxRetryScheduler scheduler;

    @Autowired
    NotificationService notificationService;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    NotificationOutboxRepository notificationOutboxRepository;

    @Value("${notification.kafka.topic}")
    String topic;

    @Test
    void retryNotificationOutbox_shouldPublishToKafka_markSent_andRemoveOutboxEntry() {
        NotificationResponse created = notificationService.save(
                new NotificationCreateRequest("alice", "hello"));
        String notificationId = created.getId().toString();

        scheduler.retryNotificationOutbox();

        ConsumerRecord<String, String> record = awaitRecord(topic, r -> notificationId.equals(r.key()));
        assertThat(record.value())
                .contains(notificationId)
                .contains("alice")
                .contains("hello");

        assertThat(notificationOutboxRepository.count()).isZero();

        NotificationModel notification = notificationRepository.findById(created.getId()).orElseThrow();
        assertThat(notification.getNotificationStatus()).isEqualTo(NotificationStatus.SENT);
    }

    @Test
    void retryNotificationOutbox_shouldRedeliver_whenOutboxDeletionFailsAfterKafkaPublish() {
        NotificationResponse created = notificationService.save(
                new NotificationCreateRequest("alice", "hello")
        );
        String notificationId = created.getId().toString();

        try {
            breakOutboxDeletion();
            scheduler.retryNotificationOutbox();
        } finally {
            restoreOutboxDeletion();
        }

        assertThat(notificationOutboxRepository.findAll()).hasSize(1);
        assertThat(notificationRepository.findById(created.getId()).orElseThrow().getNotificationStatus())
                .isEqualTo(NotificationStatus.PENDING);

        expireOutboxLease(created.getId());

        scheduler.retryNotificationOutbox();

        assertThat(notificationOutboxRepository.findAll()).isEmpty();
        assertThat(notificationRepository.findById(created.getId()).orElseThrow().getNotificationStatus())
                .isEqualTo(NotificationStatus.SENT);

        List<ConsumerRecord<String, String>> records =
                awaitRecords(topic, r -> notificationId.equals(r.key()), 2);

        assertThat(records).hasSize(2);
        assertThat(records.get(1).value()).isEqualTo(records.get(0).value());
        assertThat(records.get(0).value())
                .contains(notificationId)
                .contains("alice")
                .contains("hello");
    }

    private void breakOutboxDeletion() {
        jdbcTemplate.execute("""
            CREATE OR REPLACE FUNCTION test.fail_outbox_delete()
            RETURNS trigger AS $$
            BEGIN
                RAISE EXCEPTION 'simulated database failure';
            END;
            $$ LANGUAGE plpgsql
            """);
        jdbcTemplate.execute(
                "DROP TRIGGER IF EXISTS fail_outbox_delete ON test.notification_outbox");
        jdbcTemplate.execute("""
            CREATE TRIGGER fail_outbox_delete
            BEFORE DELETE ON test.notification_outbox
            FOR EACH ROW
            EXECUTE FUNCTION test.fail_outbox_delete()
            """);
    }

    private void restoreOutboxDeletion() {
        jdbcTemplate.execute(
                "DROP TRIGGER IF EXISTS fail_outbox_delete ON test.notification_outbox");
        jdbcTemplate.execute(
                "DROP FUNCTION IF EXISTS test.fail_outbox_delete()");
    }

    private void expireOutboxLease(UUID notificationId) {
        jdbcTemplate.update("""
            UPDATE test.notification_outbox
            SET locked_until = NOW() - INTERVAL '1 hour',
                next_retry_at = NOW() - INTERVAL '1 hour'
            WHERE notification_id = ?
            """, notificationId);
    }
}