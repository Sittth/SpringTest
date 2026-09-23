package spring.ru.springtest.service;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import spring.ru.springtest.client.metadata.dto.BookMetadataResponse;
import spring.ru.springtest.config.BookMetadataRetryProperties;
import spring.ru.springtest.controller.AbstractControllerTest;
import spring.ru.springtest.models.BookModel;
import spring.ru.springtest.models.enums.BookMetadataStatus;
import spring.ru.springtest.services.BookMetadataStateService;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookMetadataStateServiceTest extends AbstractControllerTest {

    @Autowired
    BookMetadataStateService bookMetadataStateService;

    @Autowired
    CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    BookMetadataRetryProperties retryProperties;

    @Test
    void markConfirmed_shouldSetConfirmedStatus_andResetAttempts() {
        BookModel book = pendingBook();
        book.setAttempts(3);

        bookMetadataStateService.markConfirmed(book,
                new BookMetadataResponse().publisher("Publisher").price(BigDecimal.TEN));

        assertThat(book.getMetadataStatus()).isEqualTo(BookMetadataStatus.CONFIRMED);
        assertThat(book.getAttempts()).isZero();
        assertThat(book.getNextRetryAt()).isNull();
        assertThat(book.getPublisher()).isEqualTo("Publisher");
        assertThat(book.getPrice()).isEqualByComparingTo(BigDecimal.TEN);
    }

    @Test
    void recordFailure_shouldScheduleRetry_onTransientFailure() {
        BookModel book = pendingBook();

        bookMetadataStateService.recordFailure(book, new IOException("network blip"));

        assertThat(book.getMetadataStatus()).isEqualTo(BookMetadataStatus.PENDING);
        assertThat(book.getAttempts()).isEqualTo(1);
        assertThat(book.getNextRetryAt()).isAfter(OffsetDateTime.now());
    }

    @Test
    void recordFailure_shouldMarkFailed_afterMaxTransientAttempts() {
        BookModel book = pendingBook();
        book.setAttempts(retryProperties.maxAttempts() - 1);

        bookMetadataStateService.recordFailure(book, new IOException("still failing"));

        assertThat(book.getMetadataStatus()).isEqualTo(BookMetadataStatus.FAILED);
        assertThat(book.getNextRetryAt()).isNull();
    }

    @Test
    void recordFailure_shouldMarkFailedImmediately_onPermanentFailure() {
        BookModel book = pendingBook();

        bookMetadataStateService.recordFailure(book, new ConstraintViolationException(Set.of()));

        assertThat(book.getMetadataStatus()).isEqualTo(BookMetadataStatus.FAILED);
        assertThat(book.getAttempts()).isZero();
        assertThat(book.getNextRetryAt()).isNull();
    }

    @Test
    void recordFailure_shouldDeferUntilCircuitMayClose_onCircuitOpen() {
        BookModel book = pendingBook();

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("bookMetadataPost");
        CallNotPermittedException rejection = CallNotPermittedException.createCallNotPermittedException(circuitBreaker);

        bookMetadataStateService.recordFailure(book, rejection);

        assertThat(book.getMetadataStatus()).isEqualTo(BookMetadataStatus.PENDING);
        assertThat(book.getAttempts()).isZero();
        assertThat(book.getNextRetryAt()).isAfter(OffsetDateTime.now().plusSeconds(30));
    }

    @Test
    void recordFailure_shouldThrow_onUnclassifiableFailure() {
        BookModel book = pendingBook();

        assertThatThrownBy(() -> bookMetadataStateService.recordFailure(book, new RuntimeException("unexplained")))
                .isInstanceOf(IllegalStateException.class);
    }

    private BookModel pendingBook() {
        BookModel book = new BookModel();
        book.setTitle("Some Book");
        book.setPublisher("Old Publisher");
        book.setPrice(BigDecimal.ONE);
        book.setMetadataStatus(BookMetadataStatus.PENDING);
        book.setAttempts(0);
        book.setNextRetryAt(OffsetDateTime.now());
        return book;
    }
}