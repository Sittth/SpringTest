package spring.ru.springtest.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
import spring.ru.springtest.models.CacheInvalidationQueueEntry;
import spring.ru.springtest.repositories.CacheInvalidationQueueRepository;

import java.util.UUID;

@Component
@Slf4j
public class CacheInvalidationQueueService {

    private final CacheInvalidationQueueRepository cacheInvalidationQueueRepository;
    private final CacheManager cacheManager;
    private final TransactionTemplate requiresNewTransactionTemplate;

    public CacheInvalidationQueueService(CacheInvalidationQueueRepository cacheInvalidationQueueRepository,
                                         CacheManager cacheManager,
                                         PlatformTransactionManager transactionManager) {
        this.cacheInvalidationQueueRepository = cacheInvalidationQueueRepository;
        this.cacheManager = cacheManager;
        this.requiresNewTransactionTemplate = new TransactionTemplate(transactionManager);
        this.requiresNewTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void enqueue(String cacheName, String cacheKey) {

        CacheInvalidationQueueEntry entry = new CacheInvalidationQueueEntry();
        entry.setCacheName(cacheName);
        entry.setCacheKey(cacheKey);
        CacheInvalidationQueueEntry saved = cacheInvalidationQueueRepository.save(entry);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                tryImmediateEviction(saved.getId(), cacheName, cacheKey);
            }
        });
    }

    private void tryImmediateEviction(UUID queueEntryId, String cacheName, String cacheKey) {
        try {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache == null) {
                throw new IllegalStateException("Cache not found: " + cacheName);
            }
            cache.evict(cacheKey);

            requiresNewTransactionTemplate.executeWithoutResult(status ->
                    cacheInvalidationQueueRepository.deleteById(queueEntryId));

            log.info("Cache '{}' key '{}' invalidated immediately after commit", cacheName, cacheKey);
        } catch (Exception e) {
            log.warn("Immediate post-commit eviction failed for cache '{}', key '{}', will retry via scheduler: {}",
                    cacheName, cacheKey, e.getMessage());
        }
    }
}