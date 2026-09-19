package spring.ru.springtest.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.ru.springtest.models.BookModel;
import spring.ru.springtest.models.enums.BookMetadataStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface BookRepository extends JpaRepository<BookModel, UUID> {

    List<BookModel> findByMetadataStatusAndIsDeletedFalse(BookMetadataStatus status);

    Page<BookModel> findByMetadataStatusAndNextRetryAtLessThanEqualAndIsDeletedFalse(
            BookMetadataStatus status, OffsetDateTime now, Pageable pageable);

    @Query(value = """
        UPDATE test.books
        SET locked_until = :lockUntil
        WHERE id IN (
            SELECT id
            FROM test.books
            WHERE metadata_status = 'PENDING'
              AND is_deleted = false
              AND next_retry_at <= :now
              AND (locked_until IS NULL OR locked_until <= :now)
            ORDER BY next_retry_at
            LIMIT :batchSize
            FOR UPDATE SKIP LOCKED
        )
        RETURNING *
        """, nativeQuery = true)
    List<BookModel> claimBatch(@Param("now") OffsetDateTime now,
                               @Param("lockUntil") OffsetDateTime lockUntil,
                               @Param("batchSize") int batchSize);
}
