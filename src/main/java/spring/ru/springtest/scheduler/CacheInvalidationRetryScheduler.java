package spring.ru.springtest.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spring.ru.springtest.models.CacheInvalidationQueueEntry;
import spring.ru.springtest.services.CacheInvalidationRetryService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheInvalidationRetryScheduler {

    private final CacheInvalidationRetryService cacheInvalidationRetryService;

    @Scheduled(fixedDelayString = "${cache-invalidation.retry.scheduler.fixed-delay-ms:30000}")
    public void retryPendingInvalidations() {

        List<CacheInvalidationQueueEntry> claimed = cacheInvalidationRetryService.claimBatch();

        for (CacheInvalidationQueueEntry entry : claimed) {
            try {
                cacheInvalidationRetryService.process(entry);
            } catch (Exception e) {
                log.error("Unexpected error while processing cache invalidation entry {}", entry.getId(), e);
            }
        }
    }
}
