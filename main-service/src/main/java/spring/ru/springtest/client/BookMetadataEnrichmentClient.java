package spring.ru.springtest.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import spring.ru.springtest.client.metadata.dto.BookMetadataResponse;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookMetadataEnrichmentClient {

    private final BookMetadataResilientClient bookMetadataResilientClient;

    public Optional<BookMetadataResponse> fetchMetadata(UUID bookId) {
        return bookMetadataResilientClient.fetchWithResilience(bookId);
    }

    public Optional<BookMetadataResponse> createMetadata(UUID bookId, String publisher, BigDecimal price) {
        UUID idempotencyKey = UUID.nameUUIDFromBytes(bookId.toString().getBytes());
        return bookMetadataResilientClient.createWithResilience(bookId, publisher, price, idempotencyKey);
    }
}
