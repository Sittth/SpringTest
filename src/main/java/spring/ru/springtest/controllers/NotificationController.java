package spring.ru.springtest.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import spring.ru.springtest.api.NotificationsApi;
import spring.ru.springtest.dto.create.NotificationCreateRequest;
import spring.ru.springtest.dto.response.NotificationResponse;
import spring.ru.springtest.services.NotificationService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationsApi {

    private final NotificationService notificationService;

    @Override
    public NotificationResponse createNotification(NotificationCreateRequest notificationCreateRequest) {
        return notificationService.save(notificationCreateRequest);
    }

    @Override
    public NotificationResponse getNotificationById(UUID id) {
        return notificationService.findById(id);
    }
}
