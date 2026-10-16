package spring.ru.springtest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.springtest.dto.create.NotificationCreateRequest;
import spring.ru.springtest.dto.response.NotificationResponse;
import spring.ru.springtest.exceptions.EntityNotFoundException;
import spring.ru.springtest.mapper.NotificationMapper;
import spring.ru.springtest.models.NotificationModel;
import spring.ru.springtest.models.enums.NotificationStatus;
import spring.ru.springtest.repositories.NotificationRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationOutboxService notificationOutboxService;
    private final NotificationMapper notificationMapper;

    private NotificationModel findExistingNotification(UUID id) {
        return notificationRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Notification not found with id {}", id);
                    return new EntityNotFoundException("Notification " + id);
                });
    }

    @Transactional(readOnly = true)
    public NotificationResponse findById(UUID id) {

        log.info("Search notification by id: {}", id);

        NotificationModel notification = findExistingNotification(id);

        return notificationMapper.toResponse(notification);
    }

    @Transactional
    public NotificationResponse save(NotificationCreateRequest requestCreate) {

        log.info("Save notification: {}", requestCreate);

        NotificationModel notification = notificationMapper.toEntity(requestCreate);
        notification.setNotificationStatus(NotificationStatus.PENDING);
        NotificationModel saved = notificationRepository.save(notification);

        notificationOutboxService.enqueue(saved);

        log.info("Saved notification with id {}", saved.getId());

        return notificationMapper.toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {

        log.info("Delete notification with id {}", id);

        NotificationModel notification = findExistingNotification(id);

        notificationRepository.delete(notification);

        log.info("Deleted notification with id {}", id);
    }
}
