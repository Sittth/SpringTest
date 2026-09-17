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

        List<CacheInvalidationQueueEntry> pending = cacheInvalidationQueueRepository
                .findAll(PageRequest.of(0, BATCH_SIZE)).getContent();

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
                log.warn("Retry of cache invalidation still failing for cache '{}', key '{}': {}",
                        entry.getCacheName(), entry.getCacheKey(), e.getMessage());
            }
        }
    }
}
