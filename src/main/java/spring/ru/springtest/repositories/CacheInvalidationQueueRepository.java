package spring.ru.springtest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.ru.springtest.models.CacheInvalidationQueueEntry;

import java.util.UUID;

public interface CacheInvalidationQueueRepository extends JpaRepository<CacheInvalidationQueueEntry, UUID> {
}
