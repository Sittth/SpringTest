package spring.ru.springtest.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import spring.ru.springtest.client.BookMetadataResilientClient;
import spring.ru.springtest.client.metadata.dto.BookMetadataResponse;
import spring.ru.springtest.exceptions.BookMetadataRegistrationException;
import spring.ru.springtest.mapper.AuthorMapper;
import spring.ru.springtest.models.BookModel;
import spring.ru.springtest.models.enums.BookMetadataStatus;
import spring.ru.springtest.repositories.BookRepository;
import spring.ru.springtest.services.BookMetadataRetryPolicy;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookMetadataRetryScheduler {

    private static final int BATCH_SIZE = 10;

    private final BookMetadataResilientClient bookMetadataResilientClient;
    private final AuthorMapper authorMapper;
    private final TransactionTemplate transactionTemplate;
    private final BookRepository bookRepository;
    private final BookMetadataRetryPolicy bookMetadataRetryPolicy;

    @Scheduled(fixedDelay = 60_000)
    public void retryPendingMetadata() {

        Pageable pageable = PageRequest.of(
                0, BATCH_SIZE, Sort.by("nextRetryAt").ascending());

        Page<BookModel> duePage =
                bookRepository.findBuMetadataStatusAndNextRetryAtLessThanEqualAndIsDeletedFalse(
                        BookMetadataStatus.PENDING, OffsetDateTime.now(), pageable);

        for (BookModel book : duePage.getContent()) {
            try {
                BookMetadataResponse meta = bookMetadataResilientClient.createWithResilience(
                        book.getId(),
                        book.getPublisher(),
                        book.getPrice());

                authorMapper.updateBookMetadata(meta, book);
                book.setMetadataStatus(BookMetadataStatus.CONFIRMED);
            } catch (BookMetadataRegistrationException e) {
                bookMetadataRetryPolicy.recordFailure(book, e);
            }

            transactionTemplate.executeWithoutResult(status -> bookRepository.save(book));
        }
    }
}
