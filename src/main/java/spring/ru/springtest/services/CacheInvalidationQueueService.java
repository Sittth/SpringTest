package spring.ru.springtest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.springtest.models.CacheInvalidationQueueEntry;
import spring.ru.springtest.repositories.CacheInvalidationQueueRepository;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class CacheInvalidationQueueService {

    private final CacheInvalidationQueueRepository cacheInvalidationQueueRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void enqueue(String cacheName, String cacheKey) {

        CacheInvalidationQueueEntry entry = new CacheInvalidationQueueEntry();
        entry.setCacheName(cacheName);
        entry.setCacheKey(cacheKey);
        cacheInvalidationQueueRepository.save(entry);

        log.info("Cache invalidation queued for cache '{}', key '{}'", cacheName, cacheKey);
    }
}