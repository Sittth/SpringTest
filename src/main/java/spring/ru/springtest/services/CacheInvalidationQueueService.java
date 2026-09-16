package spring.ru.springtest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import spring.ru.springtest.models.CacheInvalidationQueueEntry;
import spring.ru.springtest.repositories.CacheInvalidationQueueRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheInvalidationQueueService {

    private final CacheInvalidationQueueRepository cacheInvalidationQueueRepository;
    private final CacheManager cacheManager;

    public void enqueue(String cacheName, String cacheKey) {

        CacheInvalidationQueueEntry entry = new CacheInvalidationQueueEntry();
        entry.setCacheName(cacheName);
        entry.setCacheKey(cacheKey);
        CacheInvalidationQueueEntry saved = cacheInvalidationQueueRepository.save(entry);

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    tryImmediateEviction(saved.getId(), cacheName, cacheKey);
                }
            });
        } else {
            log.warn("enqueue() called outside an active transaction — skipping immediate post-commit eviction for cache '{}', key '{}'; scheduler will retry",
                    cacheName, cacheKey);
        }
    }

    private void tryImmediateEviction(UUID queueEntryId, String cacheName, String cacheKey) {
        try {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache == null) {
                throw new IllegalStateException("Cache not found: " + cacheName);
            }
            cache.evict(cacheKey);
            cacheInvalidationQueueRepository.deleteById(queueEntryId);
            log.info("Cache '{}' key '{}' invalidated immediately after commit", cacheName, cacheKey);
        } catch (Exception e) {
            log.warn("Immediate post-commit eviction failed for cache '{}', key '{}', will retry via scheduler: {}",
                    cacheName, cacheKey, e.getMessage());
        }
    }
}