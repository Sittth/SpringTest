package spring.ru.springtest.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import spring.ru.springtest.client.BookMetadataResilientClient;
import spring.ru.springtest.client.metadata.dto.BookMetadataResponse;
import spring.ru.springtest.controller.AbstractControllerTest;
import spring.ru.springtest.models.BookModel;
import spring.ru.springtest.services.BookMetadataEnrichmentService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookMetadataEnrichmentServiceTest extends AbstractControllerTest {

    @Autowired
    BookMetadataEnrichmentService bookMetadataEnrichmentService;

    @MockitoBean
    BookMetadataResilientClient bookMetadataResilientClient;

    @Test
    void enrich_shouldDelegateToResilientClient_withBookFields() {
        BookModel book = new BookModel();
        book.setId(UUID.randomUUID());
        book.setPublisher("Publisher");
        book.setPrice(BigDecimal.valueOf(9.99));

        BookMetadataResponse expected = new BookMetadataResponse().publisher("Publisher").price(BigDecimal.valueOf(9.99));
        when(bookMetadataResilientClient.createWithResilience(book.getId(), "Publisher", BigDecimal.valueOf(9.99)))
                .thenReturn(expected);

        BookMetadataResponse actual = bookMetadataEnrichmentService.enrich(book);

        assertThat(actual).isEqualTo(expected);
        verify(bookMetadataResilientClient).createWithResilience(book.getId(), "Publisher", BigDecimal.valueOf(9.99));
    }

    @Test
    void enrich_shouldPropagateException_fromResilientClient() {
        BookModel book = new BookModel();
        book.setId(UUID.randomUUID());
        book.setPublisher("Publisher");
        book.setPrice(BigDecimal.ONE);

        when(bookMetadataResilientClient.createWithResilience(book.getId(), "Publisher", BigDecimal.ONE))
                .thenThrow(new RuntimeException("downstream failure"));

        assertThatThrownBy(() -> bookMetadataEnrichmentService.enrich(book))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("downstream failure");
    }
}
