package spring.ru.springtest.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoggingCacheErrorHandler implements CacheErrorHandler {

    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.warn("Cache GET error on cache '{}', key '{}': {}", cache.getName(), key, exception.getMessage(), exception);
    }

    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        log.warn("Cache PUT error on cache '{}', key '{}': {}", cache.getName(), key, exception.getMessage(), exception);
    }

    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.warn("Cache EVICT error on cache '{}', key '{}': {}", cache.getName(), key, exception.getMessage(), exception);
    }

    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.warn("Cache CLEAR error on cache '{}': {}", cache.getName(), exception.getMessage(), exception);
    }
}
