package spring.ru.springtest.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import spring.ru.springtest.config.RedisConfig;
import spring.ru.springtest.controller.AbstractControllerTest;
import spring.ru.springtest.models.CacheInvalidationQueueEntry;
import spring.ru.springtest.repositories.CacheInvalidationQueueRepository;

import static org.assertj.core.api.Assertions.assertThat;

class CacheInvalidationRetrySchedulerTest extends AbstractControllerTest {

    @Autowired
    CacheInvalidationRetryScheduler scheduler;

    @Autowired
    CacheInvalidationQueueRepository cacheInvalidationQueueRepository;

    @Test
    void retryPendingInvalidations_shouldEvictAndRemoveEntry() {
        CacheInvalidationQueueEntry entry = new CacheInvalidationQueueEntry();
        entry.setCacheName(RedisConfig.AUTHOR_CACHE);
        entry.setCacheKey("some-author-id");
        entry = cacheInvalidationQueueRepository.save(entry);

        scheduler.retryPendingInvalidations();

        assertThat(cacheInvalidationQueueRepository.findById(entry.getId())).isEmpty();
    }
}
