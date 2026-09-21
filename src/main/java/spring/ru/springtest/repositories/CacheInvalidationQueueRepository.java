package spring.ru.springtest.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.ru.springtest.models.CacheInvalidationQueueEntry;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface CacheInvalidationQueueRepository extends JpaRepository<CacheInvalidationQueueEntry, UUID> {

    List<CacheInvalidationQueueEntry> findByNextRetryAtLessThanEqualOrderByNextRetryAtAsc(
            OffsetDateTime now,
            Pageable pageable
    );

    @Query(value = """
        UPDATE test.cache_invalidation_queue
        SET locked_until = :lockUntil
        WHERE id IN (
            SELECT id
            FROM test.cache_invalidation_queue
            WHERE status = 'PENDING'
              AND next_retry_at <= :now
              AND (locked_until IS NULL OR locked_until <= :now)
            ORDER BY next_retry_at
            LIMIT :batchSize
            FOR UPDATE SKIP LOCKED
        )
        RETURNING *
        """, nativeQuery = true)
    List<CacheInvalidationQueueEntry> claimBatch(@Param("now") OffsetDateTime now,
                                                 @Param("lockUntil") OffsetDateTime lockUntil,
                                                 @Param("batchSize") int batchSize);
}
