package spring.ru.springtest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import spring.ru.springtest.config.CacheInvalidationRetryProperties;
import spring.ru.springtest.models.CacheInvalidationQueueEntry;
import spring.ru.springtest.models.enums.CacheInvalidationStatus;
import spring.ru.springtest.repositories.CacheInvalidationQueueRepository;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheInvalidationRetryService {

    private static final int MAX_BACKOFF_SHIFT = 20;
    private static final int MAX_ERROR_LENGTH = 1000;

    private final CacheInvalidationQueueRepository cacheInvalidationQueueRepository;
    private final CacheManager cacheManager;
    private final TransactionTemplate transactionTemplate;
    private final CacheInvalidationRetryProperties retryProperties;

    public List<CacheInvalidationQueueEntry> claimBatch() {

        OffsetDateTime now = OffsetDateTime.now();

        CacheInvalidationRetryProperties.Scheduler settings = retryProperties.scheduler();

        List<CacheInvalidationQueueEntry> claimed = transactionTemplate.execute(status ->
                cacheInvalidationQueueRepository.claimBatch(now, now.plus(settings.lease()), settings.batchSize()));

        return claimed == null ? List.of() : claimed;
    }

    public void process(CacheInvalidationQueueEntry entry) {

        Cache cache = cacheManager.getCache(entry.getCacheName());

        if (cache == null) {
            markFailed(entry, "Unknown cache: " + entry.getCacheName());
            return;
        }

        try {
            cache.evict(entry.getCacheKey());
            complete(entry);
        } catch (Exception e) {
            recordFailure(entry, e);
        }
    }

    private void complete(CacheInvalidationQueueEntry entry) {
        transactionTemplate.executeWithoutResult(status ->
                cacheInvalidationQueueRepository.deleteById(entry.getId()));

        log.info("Cache '{}' key '{}' invalidated by retry, entry removed from queue",
                entry.getCacheName(), entry.getCacheKey());
    }

    private void recordFailure(CacheInvalidationQueueEntry entry, Exception cause) {

        int attempts = entry.getAttempts() + 1;
        int maxAttempts = retryProperties.maxAttempts();

        entry.setAttempts(attempts);
        entry.setLastError(truncate(cause.toString()));
        entry.setLockedUntil(null);

        if (attempts >= maxAttempts) {
            entry.setStatus(CacheInvalidationStatus.FAILED);
            log.error("Cache invalidation permanently failed for cache '{}', key '{}' after {} attempts",
                    entry.getCacheName(), entry.getCacheKey(), attempts, cause);
        } else {
            entry.setNextRetryAt(calculateNextRetryAt(attempts));
            log.warn("Cache invalidation failed for cache '{}', key '{}' (attempt {}/{}), next retry at {}: {}",
                    entry.getCacheName(), entry.getCacheKey(), attempts, maxAttempts,
                    entry.getNextRetryAt(), cause.getMessage());
        }

        transactionTemplate.executeWithoutResult(status ->
                cacheInvalidationQueueRepository.save(entry));
    }

    private void markFailed(CacheInvalidationQueueEntry entry, String reason) {

        entry.setStatus(CacheInvalidationStatus.FAILED);
        entry.setLastError(truncate(reason));
        entry.setLockedUntil(null);

        log.error("Cache invalidation entry {} marked as FAILED without retry: {}", entry.getId(), reason);

        transactionTemplate.executeWithoutResult(status ->
                cacheInvalidationQueueRepository.save(entry));
    }

    private OffsetDateTime calculateNextRetryAt(int attempts) {
        int shift = Math.min(attempts - 1, MAX_BACKOFF_SHIFT);

        Duration delay = retryProperties.baseDelay().multipliedBy(1L << shift);
        Duration maxDelay = retryProperties.maxDelay();

        return OffsetDateTime.now().plus(delay.compareTo(maxDelay) > 0 ? maxDelay : delay);
    }

    private String truncate(String message) {
        if (message == null || message.length() <= MAX_ERROR_LENGTH) {
            return message;
        }
        return message.substring(0, MAX_ERROR_LENGTH);
    }
}
