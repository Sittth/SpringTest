package spring.ru.springtest.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.stereotype.Component;
import spring.ru.springtest.models.CacheInvalidationQueueEntry;
import spring.ru.springtest.repositories.CacheInvalidationQueueRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingCacheErrorHandler implements CacheErrorHandler {

    private final CacheInvalidationQueueRepository cacheInvalidationQueueRepository;

    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.warn("Cache GET error on cache '{}', key '{}': {}", cache.getName(), key, exception.getMessage(), exception);
    }

    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        log.warn("Cache PUT error on cache '{}', key '{}': {}", cache.getName(), key, exception.getMessage(), exception);
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.error("Cache EVICT error on cache '{}', key '{}', queuing for retry: {}",
                cache.getName(), key, exception.getMessage(), exception);

        CacheInvalidationQueueEntry entry = new CacheInvalidationQueueEntry();
        entry.setCacheName(cache.getName());
        entry.setCacheKey(String.valueOf(key));
        cacheInvalidationQueueRepository.save(entry);
    }

    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.warn("Cache CLEAR error on cache '{}': {}", cache.getName(), exception.getMessage(), exception);
    }
}
