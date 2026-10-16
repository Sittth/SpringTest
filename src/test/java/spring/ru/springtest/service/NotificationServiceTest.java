package spring.ru.springtest.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import spring.ru.springtest.controller.AbstractControllerTest;
import spring.ru.springtest.dto.create.NotificationCreateRequest;
import spring.ru.springtest.dto.response.NotificationResponse;
import spring.ru.springtest.exceptions.EntityNotFoundException;
import spring.ru.springtest.models.NotificationModel;
import spring.ru.springtest.models.NotificationOutboxModel;
import spring.ru.springtest.models.enums.NotificationOutboxStatus;
import spring.ru.springtest.models.enums.NotificationStatus;
import spring.ru.springtest.repositories.NotificationOutboxRepository;
import spring.ru.springtest.repositories.NotificationRepository;
import spring.ru.springtest.services.NotificationService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NotificationServiceTest extends AbstractControllerTest {

    @Autowired
    NotificationService notificationService;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    NotificationOutboxRepository notificationOutboxRepository;

    @Test
    void save_shouldPersistNotificationAndOutboxEntryTogether() {
        NotificationResponse response = notificationService.save(
                new NotificationCreateRequest("alice", "hello"));

        assertThat(response.getStatus()).isEqualTo("PENDING");

        NotificationModel notification = notificationRepository.findById(response.getId()).orElseThrow();
        assertThat(notification.getNotificationStatus()).isEqualTo(NotificationStatus.PENDING);

        List<NotificationOutboxModel> outbox = notificationOutboxRepository.findAll();
        assertThat(outbox).hasSize(1);
        assertThat(outbox.get(0).getNotificationId()).isEqualTo(response.getId());
        assertThat(outbox.get(0).getNotificationOutboxStatus()).isEqualTo(NotificationOutboxStatus.PENDING);
        assertThat(outbox.get(0).getPayload())
                .contains(response.getId().toString())
                .contains("alice")
                .contains("hello");
    }

    @Test
    void findById_shouldThrow_whenNotificationNotFound() {
        assertThatThrownBy(() -> notificationService.findById(java.util.UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_shouldSoftDeleteNotification_withoutTouchingOutbox() {
        NotificationResponse created = notificationService.save(
                new NotificationCreateRequest("alice", "hello"));

        notificationService.delete(created.getId());

        NotificationModel row = notificationRepository.findById(created.getId()).orElseThrow();
        assertThat(row.isDeleted()).isTrue();

        assertThatThrownBy(() -> notificationService.findById(created.getId()))
                .isInstanceOf(EntityNotFoundException.class);

        assertThat(notificationOutboxRepository.count()).isEqualTo(1);
    }
}