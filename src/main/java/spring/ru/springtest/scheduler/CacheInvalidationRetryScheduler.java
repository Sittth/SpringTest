package spring.ru.springtest.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import spring.ru.springtest.models.CacheInvalidationQueueEntry;
import spring.ru.springtest.repositories.CacheInvalidationQueueRepository;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheInvalidationRetryScheduler {

    private static final int BATCH_SIZE = 10;

    private final CacheInvalidationQueueRepository cacheInvalidationQueueRepository;
    private final CacheManager cacheManager;
    private final TransactionTemplate transactionTemplate;

    @Scheduled(fixedDelayString = "${cache.invalidation.retry.fixed-delay-ms:30000}")
    @SchedulerLock(name = "cacheInvalidationRetryScheduler", lockAtLeastFor = "5s", lockAtMostFor = "2m")
    public void retryPendingInvalidations() {

        OffsetDateTime now = OffsetDateTime.now();

        List<CacheInvalidationQueueEntry> pending = cacheInvalidationQueueRepository
                .findByNextRetryAtLessThanEqualOrderByNextRetryAtAsc(
                        now, PageRequest.of(0, BATCH_SIZE));

        for (CacheInvalidationQueueEntry entry : pending) {
            try {
                Cache cache = cacheManager.getCache(entry.getCacheName());
                if (cache == null) {
                    throw new IllegalStateException("Cache not found: " + entry.getCacheName());
                }

                cache.evict(entry.getCacheKey());

                transactionTemplate.executeWithoutResult(status ->
                        cacheInvalidationQueueRepository.delete(entry));

            } catch (Exception e) {
                transactionTemplate.executeWithoutResult(status -> {
                    int attempts = entry.getAttempts() + 1;

                    entry.setAttempts(attempts);
                    entry.setNextRetryAt(calculateNextRetryAt(attempts));

                    cacheInvalidationQueueRepository.save(entry);
                });

                log.warn(
                        "Retry of cache invalidation failed for cache '{}', key '{}', attempt {}. " +
                                "Next retry at {}: {}",
                        entry.getCacheName(),
                        entry.getCacheKey(),
                        entry.getAttempts(),
                        entry.getNextRetryAt(),
                        e.getMessage(),
                        e
                );
            }
        }
    }

    private OffsetDateTime calculateNextRetryAt(int attempts) {
        long delaySeconds = Math.min(
                30L * (1L << Math.min(attempts - 1, 4)),
                600L
        );

        return OffsetDateTime.now().plusSeconds(delaySeconds);
    }
}
