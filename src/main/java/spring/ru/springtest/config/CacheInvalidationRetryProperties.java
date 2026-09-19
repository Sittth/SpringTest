package spring.ru.springtest.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "cache-invalidation.retry")
public record CacheInvalidationRetryProperties(

        @DefaultValue("10") @Min(1) int maxAttempts,

        @DefaultValue("30s") @NotNull Duration baseDelay,

        @DefaultValue("10m") @NotNull Duration maxDelay,

        @DefaultValue @Valid Scheduler scheduler
) {

    public record Scheduler(

            @DefaultValue("10") @Min(1) int batchSize,

            @DefaultValue("5m") @NotNull Duration lease
    ) {
    }
}
