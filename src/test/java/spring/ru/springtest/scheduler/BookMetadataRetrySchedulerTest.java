package spring.ru.springtest.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import spring.ru.springtest.client.BookMetadataResilientClient;
import spring.ru.springtest.client.metadata.dto.BookMetadataResponse;
import spring.ru.springtest.controller.AbstractControllerTest;
import spring.ru.springtest.exceptions.BookMetadataRegistrationException;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.models.BookModel;
import spring.ru.springtest.models.enums.BookMetadataStatus;
import spring.ru.springtest.repositories.AuthorRepository;
import spring.ru.springtest.repositories.BookRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class BookMetadataRetrySchedulerTest extends AbstractControllerTest {

    @Autowired
    BookMetadataRetryScheduler scheduler;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    AuthorRepository authorRepository;

    @MockitoBean
    BookMetadataResilientClient bookMetadataResilientClient;

    @Test
    void retryPendingMetadata_shouldConfirmBook_whenRegistrationSucceeds() {
        when(bookMetadataResilientClient.createWithResilience(any(), any(), any()))
                .thenReturn(new BookMetadataResponse().publisher("Publisher").price(BigDecimal.TEN));

        BookModel book = pendingBook();

        scheduler.retryPendingMetadata();

        BookModel reloaded = bookRepository.findById(book.getId()).orElseThrow();
        assertThat(reloaded.getMetadataStatus()).isEqualTo(BookMetadataStatus.CONFIRMED);
        assertThat(reloaded.getLockedUntil()).isNull();
    }

    @Test
    void retryPendingMetadata_shouldRescheduleBook_whenRegistrationFailsTransiently() {
        when(bookMetadataResilientClient.createWithResilience(any(), any(), any()))
                .thenThrow(new BookMetadataRegistrationException(UUID.randomUUID(), new IOException("boom")));

        BookModel book = pendingBook();

        scheduler.retryPendingMetadata();

        BookModel reloaded = bookRepository.findById(book.getId()).orElseThrow();
        assertThat(reloaded.getMetadataStatus()).isEqualTo(BookMetadataStatus.PENDING);
        assertThat(reloaded.getAttempts()).isEqualTo(1);
        assertThat(reloaded.getNextRetryAt()).isAfter(OffsetDateTime.now());
    }

    private BookModel pendingBook() {
        AuthorModel author = new AuthorModel();
        author.setName("Retry Author");
        author = authorRepository.save(author);

        BookModel book = new BookModel();
        book.setTitle("Retry Book");
        book.setPublisher("Publisher");
        book.setPrice(BigDecimal.TEN);
        book.setAuthor(author);
        book.setMetadataStatus(BookMetadataStatus.PENDING);
        book.setAttempts(0);
        book.setNextRetryAt(OffsetDateTime.now().minusMinutes(1));

        return bookRepository.save(book);
    }
}