package spring.ru.springtest.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.support.TransactionTemplate;
import spring.ru.springtest.controller.AbstractControllerTest;
import spring.ru.springtest.repositories.CacheInvalidationQueueRepository;
import spring.ru.springtest.services.CacheInvalidationQueueService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CacheInvalidationQueueServiceTest extends AbstractControllerTest {

    @Autowired
    CacheInvalidationQueueService cacheInvalidationQueueService;

    @Autowired
    CacheInvalidationQueueRepository cacheInvalidationQueueRepository;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Test
    void enqueue_shouldThrow_whenCalledOutsideTransaction() {
        assertThatThrownBy(() -> cacheInvalidationQueueService.enqueue("authors", "some-id"))
                .isInstanceOf(IllegalTransactionStateException.class);
    }

    @Test
    void enqueue_shouldPersistEntry_whenCalledWithinExistingTransaction() {
        transactionTemplate.executeWithoutResult(status ->
                cacheInvalidationQueueService.enqueue("authors", "some-id"));

        boolean queued = cacheInvalidationQueueRepository.findAll().stream()
                .anyMatch(entry -> entry.getCacheName().equals("authors") && entry.getCacheKey().equals("some-id"));
        assertThat(queued).isTrue();
    }
}