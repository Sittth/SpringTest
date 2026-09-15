package spring.ru.springtest.services;

import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
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

        if (isPermanentFailure(cause)) {
            book.setMetadataStatus(BookMetadataStatus.FAILED);
            log.error("Book metadata registration rejected by second-service (4xx) for book {}, marking as FAILED immediately",
                    book.getId(), cause);
            return;
        }

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

    private boolean isPermanentFailure(Throwable cause) {
        Throwable current = cause;
        while (current != null) {
            if (current instanceof ConstraintViolationException) {
                return true;
            }
            if (current instanceof FeignException feignException
                    && feignException.status() >= 400 && feignException.status() < 500) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
