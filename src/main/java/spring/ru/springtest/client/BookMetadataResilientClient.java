package spring.ru.springtest.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import spring.ru.springtest.client.metadata.dto.BookMetadataCreateRequest;
import spring.ru.springtest.client.metadata.dto.BookMetadataResponse;
import spring.ru.springtest.exceptions.BookMetadataRegistrationException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookMetadataResilientClient {

    private final BookMetadataClient bookMetadataClient;

    @Retry(name = "bookMetadataPost")
    @CircuitBreaker(name = "bookMetadataPost", fallbackMethod = "createFallback")
    public BookMetadataResponse createWithResilience(
            UUID bookId, String publisher, BigDecimal price) {

        UUID idempotencyKey = UUID.nameUUIDFromBytes(bookId.toString().getBytes());

        BookMetadataCreateRequest request = new BookMetadataCreateRequest()
                .bookId(bookId)
                .publisher(publisher)
                .price(price);

        return bookMetadataClient.createBookMetadata(idempotencyKey, request);
    }

    private BookMetadataResponse createFallback(UUID bookId, String publisher, BigDecimal price, Throwable ex) {
        log.warn("Failed to register book metadata {}: {}", bookId, ex.toString());
        throw new BookMetadataRegistrationException(bookId, ex);
    }
}
