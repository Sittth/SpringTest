package spring.ru.springtest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import spring.ru.springtest.client.NotificationKafkaClient;

@Configuration
public class KafkaConfig {

    @Bean
    public NotificationKafkaClient notificationKafkaClient(
            KafkaTemplate<String, String> kafkaTemplate,
            NotificationKafkaProperties properties) {
        return new NotificationKafkaClient(kafkaTemplate, properties);
    }
}
