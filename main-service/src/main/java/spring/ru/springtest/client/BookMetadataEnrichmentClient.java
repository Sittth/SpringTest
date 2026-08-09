package spring.ru.springtest.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import spring.ru.springtest.client.metadata.dto.BookMetadataCreateRequest;
import spring.ru.springtest.client.metadata.dto.BookMetadataResponse;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookMetadataEnrichmentClient {

    private final BookMetadataClient bookMetadataClient;

    public Optional<BookMetadataResponse> fetchMetadata(UUID bookId) {
        return fetchWithResilience(bookId);
    }

    @Retry(name = "bookMetadataGet")
    @CircuitBreaker(name = "bookMetadataGet", fallbackMethod = "fetchFallback")
    protected Optional<BookMetadataResponse> fetchWithResilience(UUID bookId) {
        return Optional.ofNullable(bookMetadataClient.getBookMetadataByBookId(bookId));
    }

    private Optional<BookMetadataResponse> fetchFallback(UUID bookId, Throwable ex) {
        log.warn("Second-service is unavailable, skipping book enrichment {}: {}", bookId, ex.toString());
        return Optional.empty();
    }

    public Optional<BookMetadataResponse> createMetadata(UUID bookId, String publisher, BigDecimal price) {
        UUID idempotencyKey = UUID.randomUUID();
        return createWithResilience(bookId, publisher, price, idempotencyKey);
    }

    @Retry(name = "bookMetadataPost")
    @CircuitBreaker(name = "bookMetadataPost", fallbackMethod = "createFallback")
    protected Optional<BookMetadataResponse> createWithResilience(
            UUID bookId, String publisher, BigDecimal price, UUID idempotencyKey) {

        BookMetadataCreateRequest request = new BookMetadataCreateRequest()
                .bookId(bookId)
                .publisher(publisher)
                .price(price);

        return Optional.ofNullable(bookMetadataClient.createBookMetadata(idempotencyKey, request));
    }

    private Optional<BookMetadataResponse> createFallback(
            UUID bookId, String publisher, BigDecimal price, UUID idempotencyKey, Throwable ex) {
        log.warn("Failed to register book metadata {} (key={}): {}", bookId, idempotencyKey, ex.toString());
        return Optional.empty();
    }
}
