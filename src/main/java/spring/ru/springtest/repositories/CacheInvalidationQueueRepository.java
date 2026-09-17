package spring.ru.springtest.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import spring.ru.springtest.models.CacheInvalidationQueueEntry;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface CacheInvalidationQueueRepository extends JpaRepository<CacheInvalidationQueueEntry, UUID> {

    List<CacheInvalidationQueueEntry> findByNextRetryAtLessThanEqualOrderByNextRetryAtAsc(
            OffsetDateTime now,
            Pageable pageable
    );
}
