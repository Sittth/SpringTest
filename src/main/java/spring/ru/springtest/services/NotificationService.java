package spring.ru.springtest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.springtest.dto.create.NotificationCreateRequest;
import spring.ru.springtest.dto.response.NotificationResponse;
import spring.ru.springtest.exceptions.EntityNotFoundException;
import spring.ru.springtest.kafka.event.NotificationEvent;
import spring.ru.springtest.mapper.NotificationMapper;
import spring.ru.springtest.models.NotificationModel;
import spring.ru.springtest.models.NotificationOutboxModel;
import spring.ru.springtest.models.enums.NotificationOutboxStatus;
import spring.ru.springtest.models.enums.NotificationStatus;
import spring.ru.springtest.repositories.NotificationOutboxRepository;
import spring.ru.springtest.repositories.NotificationRepository;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationOutboxRepository notificationOutboxRepository;
    private final NotificationMapper notificationMapper;
    private final JsonMapper jsonMapper;

    private NotificationModel findExistingNotification(UUID id) {
        return notificationRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Notification not found with id {}", id);
                    return new EntityNotFoundException("Notification " + id);
                });
    }

    private NotificationOutboxModel buildOutboxEntry(NotificationModel notification) {
        NotificationEvent event = new NotificationEvent(
                notification.getId(), notification.getRecipient(), notification.getMessage()
        );

        NotificationOutboxModel entry = new NotificationOutboxModel();
        entry.setNotificationId(notification.getId());
        entry.setPayload(jsonMapper.writeValueAsString(event));
        entry.setNotificationOutboxStatus(NotificationOutboxStatus.PENDING);

        return entry;
    }

    @Transactional(readOnly = true)
    public NotificationResponse findById(UUID id) {

        log.info("Search notification by id: {}", id);

        NotificationModel notification = findExistingNotification(id);

        return notificationMapper.toResponse(notification);
    }

    public NotificationResponse save(NotificationCreateRequest requestCreate) {

        log.info("Save notification: {}", requestCreate);

        NotificationModel notification = notificationMapper.toEntity(requestCreate);
        notification.setNotificationStatus(NotificationStatus.PENDING);
        NotificationModel saved  = notificationRepository.save(notification);

        NotificationOutboxModel notificationEntry = buildOutboxEntry(saved);
        notificationOutboxRepository.save(notificationEntry);

        log.info("Saved notification with id {}", saved.getId());

        return notificationMapper.toResponse(saved);
    }
}
