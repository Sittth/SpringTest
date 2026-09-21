package spring.ru.springtest.services;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import spring.ru.springtest.client.FailureClassifier;
import spring.ru.springtest.client.metadata.dto.BookMetadataResponse;
import spring.ru.springtest.config.BookMetadataRetryProperties;
import spring.ru.springtest.mapper.AuthorMapper;
import spring.ru.springtest.models.BookModel;
import spring.ru.springtest.models.enums.BookMetadataStatus;
import spring.ru.springtest.models.enums.FailureType;

import java.time.Duration;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookMetadataStateService {

    private static final int MAX_BACKOFF_SHIFT = 20;

    private final AuthorMapper authorMapper;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final BookMetadataRetryProperties retryProperties;

    public void markConfirmed(BookModel book, BookMetadataResponse meta) {
        authorMapper.updateBookMetadata(meta, book);
        book.setMetadataStatus(BookMetadataStatus.CONFIRMED);
    }

    public void recordFailure(BookModel book, Throwable cause) {

        FailureType type = FailureClassifier.classify(cause);

        switch (type) {
            case CIRCUIT_OPEN -> deferUntilCircuitMayClose(book, cause);
            case PERMANENT -> markFailedImmediately(book, cause);
            case TRANSIENT, UNKNOWN -> scheduleRetryOrFail(book, cause, type);
        }
    }

    private void deferUntilCircuitMayClose(BookModel book, Throwable cause) {
        CallNotPermittedException rejection = findCircuitBreakerRejection(cause);
        Duration delay = openStateWaitDuration(rejection);

        book.setNextRetryAt(OffsetDateTime.now().plus(delay));
        log.warn("Book metadata registration rejected for book {} by {}, next attempt in {}",
                book.getId(), rejection.getMessage(), delay);
    }

    private void markFailedImmediately(BookModel book, Throwable cause) {
        book.setMetadataStatus(BookMetadataStatus.FAILED);
        log.error("Book metadata registration permanently rejected for book {}, marking as FAILED immediately",
                book.getId(), cause);
    }

    private void scheduleRetryOrFail(BookModel book, Throwable cause, FailureType type) {
        int attempts = book.getAttempts() + 1;
        book.setAttempts(attempts);

        int maxAttempts = retryProperties.maxAttempts();

        if (attempts >= maxAttempts) {
            book.setMetadataStatus(BookMetadataStatus.FAILED);
            log.error("Book metadata registration permanently failed for book {} after {} attempts ({})",
                    book.getId(), attempts, type, cause);
            return;
        }

        Duration delay = backoffDelay(attempts);
        book.setNextRetryAt(OffsetDateTime.now().plus(delay));

        if (type == FailureType.UNKNOWN) {
            log.error("Unclassified failure for book {} (attempt {}/{}), next retry at {}",
                    book.getId(), attempts, maxAttempts, book.getNextRetryAt(), cause);
        } else {
            log.warn("Transient failure for book {} (attempt {}/{}), next retry at {}",
                    book.getId(), attempts, maxAttempts, book.getNextRetryAt(), cause);
        }
    }

    private Duration backoffDelay(int attempts) {
        int shift = Math.min(attempts - 1, MAX_BACKOFF_SHIFT);
        return retryProperties.baseDelay().multipliedBy(1L << shift);
    }

    private Duration openStateWaitDuration(CallNotPermittedException rejection) {
        CircuitBreaker circuitBreaker =
                circuitBreakerRegistry.circuitBreaker(rejection.getCausingCircuitBreakerName());

        long waitMillis = circuitBreaker.getCircuitBreakerConfig()
                .getWaitIntervalFunctionInOpenState()
                .apply(1);

        return Duration.ofMillis(waitMillis);
    }

    private CallNotPermittedException findCircuitBreakerRejection(Throwable cause) {
        Throwable current = cause;
        while (current != null) {
            if (current instanceof CallNotPermittedException rejection) {
                return rejection;
            }
            current = current.getCause();
        }
        throw new IllegalStateException("CIRCUIT_OPEN without CallNotPermittedException in cause chain");
    }
}
