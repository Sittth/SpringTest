package spring.ru.springtest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.ru.springtest.models.NotificationOutboxModel;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface NotificationOutboxRepository extends JpaRepository<NotificationOutboxModel, UUID> {

    @Query(value = """
        UPDATE test.notification_outbox
        SET locked_until = :lockUntil
        WHERE id IN (
            SELECT id
            FROM test.notification_outbox
            WHERE status = 'PENDING'
                AND next_retry_at <= :now
                AND (locked_until IS NULL OR locked_until <= :now)
            ORDER BY next_retry_at
            LIMIT :batchSize
            FOR UPDATE SKIP LOCKED
        )
        RETURNING *
        """, nativeQuery = true)
    List<NotificationOutboxModel> claimBatch(@Param("now") OffsetDateTime now,
                                             @Param("lockUntil") OffsetDateTime lockUntil,
                                             @Param("batchSize") int batchSize);
}
