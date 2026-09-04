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
public class BookMetadataResilientClient {

    private final BookMetadataClient bookMetadataClient;

    @Retry(name = "bookMetadataGet")
    @CircuitBreaker(name = "bookMetadataGet", fallbackMethod = "fetchFallback")
    public Optional<BookMetadataResponse> fetchWithResilience(UUID bookId) {
        return Optional.ofNullable(bookMetadataClient.getBookMetadataByBookId(bookId));
    }

    public Optional<BookMetadataResponse> fetchFallback(UUID bookId, Throwable ex) {
        log.warn("Second-service is unavailable, skipping book enrichment {}: {}", bookId, ex.toString());
        return Optional.empty();
    }

    @Retry(name = "bookMetadataPost")
    @CircuitBreaker(name = "bookMetadataPost", fallbackMethod = "createFallback")
    public Optional<BookMetadataResponse> createWithResilience(
            UUID bookId, String publisher, BigDecimal price, UUID idempotencyKey) {

        BookMetadataCreateRequest request = new BookMetadataCreateRequest()
                .bookId(bookId)
                .publisher(publisher)
                .price(price);

        return Optional.ofNullable(bookMetadataClient.createBookMetadata(idempotencyKey, request));
    }

    public Optional<BookMetadataResponse> createFallback(
            UUID bookId, String publisher, BigDecimal price, UUID idempotencyKey, Throwable ex) {
        log.warn("Failed to register book metadata {} (key={}): {}", bookId, idempotencyKey, ex.toString());
        return Optional.empty();
    }
}
