package spring.ru.springtest.service;

import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import spring.ru.springtest.client.BookMetadataResilientClient;
import spring.ru.springtest.client.metadata.dto.BookMetadataResponse;
import spring.ru.springtest.controller.AbstractControllerTest;
import spring.ru.springtest.exceptions.BookMetadataRegistrationException;
import spring.ru.springtest.models.BookModel;
import spring.ru.springtest.models.enums.BookMetadataStatus;
import spring.ru.springtest.services.BookMetadataRegistrationService;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class BookMetadataRegistrationServiceTest extends AbstractControllerTest {

    @Autowired
    BookMetadataRegistrationService bookMetadataRegistrationService;

    @MockitoBean
    BookMetadataResilientClient bookMetadataResilientClient;

    @Test
    void register_shouldConfirmBook_whenClientSucceeds() {
        when(bookMetadataResilientClient.createWithResilience(any(), any(), any()))
                .thenReturn(new BookMetadataResponse().publisher("Publisher").price(BigDecimal.TEN));

        BookModel book = pendingBook();

        bookMetadataRegistrationService.register(book);

        assertThat(book.getMetadataStatus()).isEqualTo(BookMetadataStatus.CONFIRMED);
        assertThat(book.getPublisher()).isEqualTo("Publisher");
    }

    @Test
    void register_shouldRescheduleBook_whenClientThrowsWrappedRegistrationException() {
        when(bookMetadataResilientClient.createWithResilience(any(), any(), any()))
                .thenThrow(new BookMetadataRegistrationException(UUID.randomUUID(), new IOException("boom")));

        BookModel book = pendingBook();

        bookMetadataRegistrationService.register(book);

        assertThat(book.getMetadataStatus()).isEqualTo(BookMetadataStatus.PENDING);
        assertThat(book.getAttempts()).isEqualTo(1);
        assertThat(book.getNextRetryAt()).isAfter(OffsetDateTime.now());
    }

    @Test
    void register_shouldRescheduleBook_whenClientThrowsFeignExceptionDirectly() {
        Request request = Request.create(Request.HttpMethod.POST, "/book-metadata",
                Map.of(), null, StandardCharsets.UTF_8, null);
        Response response = Response.builder()
                .status(503)
                .reason("Service Unavailable")
                .request(request)
                .build();

        when(bookMetadataResilientClient.createWithResilience(any(), any(), any()))
                .thenThrow(FeignException.errorStatus("createBookMetadata", response));

        BookModel book = pendingBook();

        bookMetadataRegistrationService.register(book);

        assertThat(book.getMetadataStatus()).isEqualTo(BookMetadataStatus.PENDING);
        assertThat(book.getAttempts()).isEqualTo(1);
    }

    private BookModel pendingBook() {
        BookModel book = new BookModel();
        book.setId(UUID.randomUUID());
        book.setTitle("Some Book");
        book.setPublisher("Old Publisher");
        book.setPrice(BigDecimal.ONE);
        book.setMetadataStatus(BookMetadataStatus.PENDING);
        book.setAttempts(0);
        book.setNextRetryAt(OffsetDateTime.now());
        return book;
    }
}