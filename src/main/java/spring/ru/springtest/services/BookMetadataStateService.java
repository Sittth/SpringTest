package spring.ru.springtest.services;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import spring.ru.springtest.client.metadata.dto.BookMetadataResponse;
import spring.ru.springtest.config.BookMetadataRetryProperties;
import spring.ru.springtest.mapper.AuthorMapper;
import spring.ru.springtest.models.BookModel;
import spring.ru.springtest.models.enums.BookMetadataStatus;

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

        CallNotPermittedException rejection = findCircuitBreakerRejection(cause);

        if (rejection != null) {
            Duration delay = openStateWaitDuration(rejection);
            book.setNextRetryAt(OffsetDateTime.now().plus(delay));
            log.warn("Book metadata registration rejected for book {} by {}, next attempt in {}",
                    book.getId(), rejection.getMessage(), delay);
            return;
        }

        if (isPermanentFailure(cause)) {
            book.setMetadataStatus(BookMetadataStatus.FAILED);
            log.error("Book metadata registration rejected by second-service (4xx) for book {}, marking as FAILED immediately",
                    book.getId(), cause);
            return;
        }

        int attempts = book.getAttempts() + 1;
        book.setAttempts(attempts);

        int maxAttempts = retryProperties.maxAttempts();

        if (attempts >= maxAttempts) {
            book.setMetadataStatus(BookMetadataStatus.FAILED);
            log.error("Book metadata registration permanently failed for book {} after {} attempts",
                    book.getId(), attempts, cause);
        } else {
            Duration delay = backoffDelay(attempts);
            book.setNextRetryAt(OffsetDateTime.now().plus(delay));
            log.warn("Book metadata registration failed for book {} (attempt {}/{}), next retry at {}",
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

    private CallNotPermittedException findCircuitBreakerRejection(Throwable cause) {
        Throwable current = cause;
        while (current != null) {
            if (current instanceof CallNotPermittedException rejection) {
                return rejection;
            }
            current = current.getCause();
        }
        return null;
    }
}
