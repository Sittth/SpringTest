package spring.ru.springtest.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookMetadataRetryScheduler {

    private final BookMetadataResilientClient bookMetadataResilientClient;
    private final AuthorMapper authorMapper;
    private final TransactionTemplate transactionTemplate;
    private final BookRepository bookRepository;

    @Scheduled(fixedDelay = 60_000)
    public void retryPendingMetadata() {

        List<BookModel> pendingBooks =
                bookRepository.findByMetadataStatusAndIsDeletedFalse(BookMetadataStatus.PENDING);

        for (BookModel book : pendingBooks) {
            try {
                BookMetadataResponse meta = bookMetadataResilientClient.createWithResilience(
                        book.getId(),
                        book.getPublisher(),
                        book.getPrice());

                authorMapper.updateBookMetadata(meta, book);
                book.setMetadataStatus(BookMetadataStatus.CONFIRMED);

                transactionTemplate.executeWithoutResult(status -> bookRepository.save(book));
            } catch (BookMetadataRegistrationException e) {
                log.warn("Book metadata registration failed for book {}, leaving status PENDING for later retry", book.getId(), e);
            }
        }
    }
}
