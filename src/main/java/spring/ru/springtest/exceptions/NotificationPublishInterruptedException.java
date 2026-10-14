package spring.ru.springtest.exceptions;

import java.util.UUID;

public class NotificationPublishInterruptedException extends NotificationPublishException {
    public NotificationPublishInterruptedException(UUID notificationId, Throwable cause) {
        super(notificationId, cause);
    }
}
