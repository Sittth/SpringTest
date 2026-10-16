package spring.ru.springtest.exceptions;

import java.util.UUID;

public class NotificationPublishException extends AppException {

    public NotificationPublishException(UUID notificationId, Throwable cause) {
        super("Failed to publish notification " + notificationId + " to Kafka", cause);
    }
}
