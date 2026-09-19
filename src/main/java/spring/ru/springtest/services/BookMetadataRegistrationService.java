package spring.ru.springtest.services;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.ru.springtest.client.metadata.dto.BookMetadataResponse;
import spring.ru.springtest.exceptions.BookMetadataRegistrationException;
import spring.ru.springtest.models.BookModel;

@Service
@RequiredArgsConstructor
public class BookMetadataRegistrationService {

    private final BookMetadataEnrichmentService bookMetadataEnrichmentService;
    private final BookMetadataStateService bookMetadataStateService;

    public void register(BookModel book) {

        BookMetadataResponse meta;
        try {
            meta = bookMetadataEnrichmentService.enrich(book);
        } catch (BookMetadataRegistrationException | FeignException e) {
            bookMetadataStateService.recordFailure(book, e);
            return;
        }

        bookMetadataStateService.markConfirmed(book, meta);
    }
}
