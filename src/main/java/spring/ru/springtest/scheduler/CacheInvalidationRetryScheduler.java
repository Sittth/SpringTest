package spring.ru.springtest.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spring.ru.springtest.models.CacheInvalidationQueueEntry;
import spring.ru.springtest.services.CacheInvalidationRetryService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheInvalidationRetryScheduler {

    private static final int BATCH_SIZE = 10;

    private final CacheInvalidationRetryService cacheInvalidationRetryService;

    @Scheduled(fixedDelayString = "${cache.invalidation.retry.fixed-delay-ms:30000}")
    @SchedulerLock(name = "cacheInvalidationRetryScheduler", lockAtLeastFor = "5s", lockAtMostFor = "2m")
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
