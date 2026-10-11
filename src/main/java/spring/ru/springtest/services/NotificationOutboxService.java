package spring.ru.springtest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.springtest.dto.event.NotificationEvent;
import spring.ru.springtest.models.NotificationModel;
import spring.ru.springtest.models.NotificationOutboxModel;
import spring.ru.springtest.models.enums.NotificationOutboxStatus;
import spring.ru.springtest.repositories.NotificationOutboxRepository;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationOutboxService {

    private final JsonMapper jsonMapper;
    private final NotificationOutboxRepository notificationOutboxRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void enqueue(NotificationModel notification) {
        NotificationEvent event = new NotificationEvent(
                notification.getId(), notification.getRecipient(), notification.getMessage());

        NotificationOutboxModel entry = new NotificationOutboxModel();
        entry.setNotificationId(notification.getId());
        entry.setPayload(jsonMapper.writeValueAsString(event));
        entry.setNotificationOutboxStatus(NotificationOutboxStatus.PENDING);

        notificationOutboxRepository.save(entry);

        log.info("Outbox entry queued for notification {}", notification.getId());
    }
}
