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
}