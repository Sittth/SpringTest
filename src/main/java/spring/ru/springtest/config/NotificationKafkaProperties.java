package spring.ru.springtest.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "notification.kafka")
public record NotificationKafkaProperties (
        @NotBlank String topic,
        @DefaultValue("10s") @NotNull Duration sendTimeout)
{}
