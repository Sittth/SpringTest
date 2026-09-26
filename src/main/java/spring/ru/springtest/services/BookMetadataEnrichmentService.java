package spring.ru.springtest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spring.ru.springtest.client.BookMetadataResilientClient;
import spring.ru.springtest.client.metadata.dto.BookMetadataResponse;
import spring.ru.springtest.models.BookModel;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookMetadataEnrichmentService {

    private final BookMetadataResilientClient bookMetadataResilientClient;

    public BookMetadataResponse enrich(BookModel book) {
        return bookMetadataResilientClient.createWithResilience(
                book.getId(),
                book.getPublisher(),
                book.getPrice()
        );
    }
}
