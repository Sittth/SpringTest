package spring.ru.springtest.client;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import spring.ru.springtest.config.NotificationKafkaProperties;
import spring.ru.springtest.exceptions.NotificationPublishException;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
public class NotificationKafkaClient {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final NotificationKafkaProperties properties;

    public void send(UUID notificationId, String payload) {
        try {
            kafkaTemplate
                    .send(properties.topic(), notificationId.toString(), payload)
                    .get(properties.sendTimeout().toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NotificationPublishException(notificationId, e);
        } catch (ExecutionException | TimeoutException e) {
            throw new NotificationPublishException(notificationId, e);
        }
    }
}
