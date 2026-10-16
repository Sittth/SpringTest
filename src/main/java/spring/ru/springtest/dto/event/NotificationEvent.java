package spring.ru.springtest.dto.event;

import java.util.UUID;

public record NotificationEvent(UUID notificationId, String recipient, String message) {}