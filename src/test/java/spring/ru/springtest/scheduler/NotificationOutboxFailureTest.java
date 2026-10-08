package spring.ru.springtest.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.kafka.core.KafkaTemplate;
import spring.ru.springtest.config.NotificationOutboxRetryProperties;
import spring.ru.springtest.controller.AbstractControllerTest;
import spring.ru.springtest.dto.create.NotificationCreateRequest;
import spring.ru.springtest.dto.response.NotificationResponse;
import spring.ru.springtest.models.NotificationOutboxModel;
import spring.ru.springtest.models.enums.NotificationOutboxStatus;
import spring.ru.springtest.models.enums.NotificationStatus;
import spring.ru.springtest.repositories.NotificationOutboxRepository;
import spring.ru.springtest.repositories.NotificationRepository;
import spring.ru.springtest.services.NotificationService;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationOutboxFailureTest extends AbstractControllerTest {

    @Autowired
    NotificationOutboxRetryScheduler scheduler;

    @Autowired
    NotificationService notificationService;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    NotificationOutboxRepository notificationOutboxRepository;

    @Autowired
    NotificationOutboxRetryProperties retryProperties;

    @MockitoBean
    KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void retryNotificationOutbox_shouldScheduleBackoff_whenKafkaFails() {
        failKafka();
        NotificationResponse created = notificationService.save(
                new NotificationCreateRequest("alice", "hello"));

        scheduler.retryNotificationOutbox();

        NotificationOutboxModel entry = notificationOutboxRepository.findAll().get(0);
        assertThat(entry.getNotificationOutboxStatus()).isEqualTo(NotificationOutboxStatus.PENDING);
        assertThat(entry.getAttempts()).isEqualTo(1);
        assertThat(entry.getLockedUntil()).isNull();
        assertThat(entry.getNextRetryAt()).isAfter(OffsetDateTime.now());
        assertThat(notificationRepository.findById(created.getId()).orElseThrow().getNotificationStatus())
                .isEqualTo(NotificationStatus.PENDING);

        scheduler.retryNotificationOutbox();

        verify(kafkaTemplate, times(1)).send(anyString(), anyString(), anyString());
        assertThat(notificationOutboxRepository.findAll().get(0).getAttempts()).isEqualTo(1);
    }

    @Test
    void retryNotificationOutbox_shouldMarkFailed_whenAttemptsExhausted() {
        failKafka();
        NotificationResponse created = notificationService.save(
                new NotificationCreateRequest("alice", "hello"));

        NotificationOutboxModel entry = notificationOutboxRepository.findAll().get(0);
        entry.setAttempts(retryProperties.maxAttempts() - 1);
        notificationOutboxRepository.save(entry);

        scheduler.retryNotificationOutbox();

        NotificationOutboxModel reloaded = notificationOutboxRepository.findAll().get(0);
        assertThat(reloaded.getNotificationOutboxStatus()).isEqualTo(NotificationOutboxStatus.FAILED);
        assertThat(reloaded.getNextRetryAt()).isNull();
        assertThat(notificationRepository.findById(created.getId()).orElseThrow().getNotificationStatus())
                .isEqualTo(NotificationStatus.FAILED);
    }

    private void failKafka() {
        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("broker down")));
    }
}