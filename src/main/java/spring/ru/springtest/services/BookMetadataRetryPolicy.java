package spring.ru.springtest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import spring.ru.springtest.models.BookModel;
import spring.ru.springtest.models.enums.BookMetadataStatus;

import java.awt.print.Book;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookMetadataRetryPolicy {

    private static final int MAX_ATTEMPTS = 5;

    public void recordFailure(BookModel book, Throwable cause) {

        int attempts = book.getAttempts() + 1;
        book.setAttempts(attempts);

        if (attempts >= MAX_ATTEMPTS) {
            book.setMetadataStatus(BookMetadataStatus.FAILED);
            log.error("Book metadata registration permanently failed for book {} after {} attempts",
                    book.getId(), attempts, cause);
        } else {
            long delayMinutes = 5L * (1L << (attempts - 1));
            book.setNextRetryAt(OffsetDateTime.now().plusMinutes(delayMinutes));
            log.warn("Book metadata registration failed for book {} (attempt {}/{}), next retry at {}",
                    book.getId(), attempts, MAX_ATTEMPTS, book.getNextRetryAt(), cause);
        }
    }
}
