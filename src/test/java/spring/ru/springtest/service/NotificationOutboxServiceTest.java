package spring.ru.springtest.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.support.TransactionTemplate;
import spring.ru.springtest.controller.AbstractControllerTest;
import spring.ru.springtest.models.NotificationModel;
import spring.ru.springtest.models.NotificationOutboxModel;
import spring.ru.springtest.models.enums.NotificationOutboxStatus;
import spring.ru.springtest.models.enums.NotificationStatus;
import spring.ru.springtest.repositories.NotificationOutboxRepository;
import spring.ru.springtest.repositories.NotificationRepository;
import spring.ru.springtest.services.NotificationOutboxService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class NotificationOutboxServiceTest extends AbstractControllerTest {

    @Autowired
    NotificationOutboxService notificationOutboxService;

    @Autowired
    NotificationOutboxRepository notificationOutboxRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Autowired
    JsonMapper jsonMapper;

    @Test
    void enqueue_shouldThrow_whenCalledOutsideTransaction() {
        assertThatThrownBy(() -> notificationOutboxService.enqueue(newNotification()))
                .isInstanceOf(IllegalTransactionStateException.class);

        assertThat(notificationOutboxRepository.count()).isZero();
    }

    @Test
    void enqueue_shouldPersistPendingEntry_whenCalledWithinExistingTransaction() throws Exception {
        NotificationModel saved = transactionTemplate.execute(status -> {
            NotificationModel notification = notificationRepository.save(newNotification());
            notificationOutboxService.enqueue(notification);
            return notification;
        });

        List<NotificationOutboxModel> outbox = notificationOutboxRepository.findAll();
        assertThat(outbox).hasSize(1);

        NotificationOutboxModel entry = outbox.get(0);
        assertThat(entry.getNotificationId()).isEqualTo(saved.getId());
        assertThat(entry.getNotificationOutboxStatus()).isEqualTo(NotificationOutboxStatus.PENDING);
        assertThat(entry.getAttempts()).isZero();

        JsonNode payload = jsonMapper.readTree(entry.getPayload());
        assertThat(payload.get("notificationId").asString()).isEqualTo(saved.getId().toString());
        assertThat(payload.get("recipient").asString()).isEqualTo("bob");
        assertThat(payload.get("message").asString()).isEqualTo("hello");
    }

    private NotificationModel newNotification() {
        NotificationModel notification = new NotificationModel();
        notification.setRecipient("bob");
        notification.setMessage("hello");
        notification.setNotificationStatus(NotificationStatus.PENDING);
        return notification;
    }
}
