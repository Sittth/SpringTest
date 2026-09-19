package spring.ru.springtest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
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

    private static final int BATCH_SIZE = 10;
    private static final int MAX_ATTEMPTS = 10;

    private static final Duration LEASE = Duration.ofMinutes(5);

    private static final long BASE_DELAY_SECONDS = 30L;
    private static final long MAX_DELAY_SECONDS = 600L;
    private static final int MAX_ERROR_LENGTH = 1000;

    private final CacheInvalidationQueueRepository cacheInvalidationQueueRepository;
    private final CacheManager cacheManager;
    private final TransactionTemplate transactionTemplate;

    public List<CacheInvalidationQueueEntry> claimBatch() {

        OffsetDateTime now = OffsetDateTime.now();

        List<CacheInvalidationQueueEntry> claimed = transactionTemplate.execute(status ->
                cacheInvalidationQueueRepository.claimBatch(now, now.plus(LEASE), BATCH_SIZE));

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
        entry.setAttempts(attempts);
        entry.setLastError(truncate(cause.toString()));
        entry.setLockedUntil(null);

        if (attempts >= MAX_ATTEMPTS) {
            entry.setStatus(CacheInvalidationStatus.FAILED);
            log.error("Cache invalidation permanently failed for cache '{}', key '{}' after {} attempts",
                    entry.getCacheName(), entry.getCacheKey(), attempts, cause);
        } else {
            entry.setNextRetryAt(calculateNextRetryAt(attempts));
            log.warn("Cache invalidation failed for cache '{}', key '{}' (attempt {}/{}), next retry at {}: {}",
                    entry.getCacheName(), entry.getCacheKey(), attempts, MAX_ATTEMPTS,
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
        long delaySeconds = Math.min(
                BASE_DELAY_SECONDS * (1L << Math.min(attempts - 1, 10)),
                MAX_DELAY_SECONDS
        );

        return OffsetDateTime.now().plusSeconds(delaySeconds);
    }

    private String truncate(String message) {
        if (message == null || message.length() <= MAX_ERROR_LENGTH) {
            return message;
        }
        return message.substring(0, MAX_ERROR_LENGTH);
    }
}
