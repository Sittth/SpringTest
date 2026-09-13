package spring.ru.springtest.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import spring.ru.springtest.models.BookModel;
import spring.ru.springtest.models.enums.BookMetadataStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface BookRepository extends JpaRepository<BookModel, UUID> {

    List<BookModel> findByMetadataStatusAndIsDeletedFalse(BookMetadataStatus status);

    Page<BookModel> findBuMetadataStatusAndNextRetryAtLessThanEqualAndIsDeletedFalse(
            BookMetadataStatus status, OffsetDateTime now, Pageable pageable);
}
