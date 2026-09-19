package spring.ru.springtest.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import spring.ru.springtest.models.BookModel;
import spring.ru.springtest.repositories.BookRepository;
import spring.ru.springtest.services.BookMetadataEnrichmentService;
import spring.ru.springtest.services.BookMetadataRegistrationService;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookMetadataRetryScheduler {

    private static final int BATCH_SIZE = 10;

    private static final Duration LEASE = Duration.ofMinutes(10);

    private final BookRepository bookRepository;
    private final TransactionTemplate transactionTemplate;
    private final BookMetadataRegistrationService bookMetadataRegistrationService;

    @Scheduled(fixedDelayString = "${book.metadata.retry.fixed-delay-ms:30000}")
    @SchedulerLock(
            name = "bookMetadataRetryScheduler",
            lockAtLeastFor = "5s",
            lockAtMostFor = "2m"
    )
    public void retryPendingMetadata() {

        OffsetDateTime now = OffsetDateTime.now();

        List<BookModel> claimed = transactionTemplate.execute(status ->
                bookRepository.claimBatch(now, now.plus(LEASE), BATCH_SIZE));

        if (claimed == null || claimed.isEmpty()) {
            return;
        }

        for (BookModel book : claimed) {
            try {
                retryBookMetadata(book);
            } catch (Exception e) {
                log.error("Unexpected error while retrying metadata for book {}", book.getId(), e);
            }
        }
    }

    private void retryBookMetadata(BookModel book) {

        bookMetadataRegistrationService.register(book);

        book.setLockedUntil(null);

        transactionTemplate.executeWithoutResult(status ->
                bookRepository.save(book)
        );
    }
}
