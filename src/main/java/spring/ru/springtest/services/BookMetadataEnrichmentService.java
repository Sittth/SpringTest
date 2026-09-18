package spring.ru.springtest.services;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spring.ru.springtest.client.BookMetadataResilientClient;
import spring.ru.springtest.client.metadata.dto.BookMetadataResponse;
import spring.ru.springtest.exceptions.BookMetadataRegistrationException;
import spring.ru.springtest.mapper.AuthorMapper;
import spring.ru.springtest.models.BookModel;
import spring.ru.springtest.models.enums.BookMetadataStatus;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookMetadataEnrichmentService {

    private final BookMetadataResilientClient bookMetadataResilientClient;
    private final AuthorMapper authorMapper;
    private final BookMetadataRetryPolicy bookMetadataRetryPolicy;

    public void enrichBook(BookModel book) {
        try {
            BookMetadataResponse meta =
                    bookMetadataResilientClient.createWithResilience(
                            book.getId(),
                            book.getPublisher(),
                            book.getPrice()
                    );

            authorMapper.updateBookMetadata(meta, book);
            book.setMetadataStatus(BookMetadataStatus.CONFIRMED);

        } catch (BookMetadataRegistrationException | FeignException e) {
            bookMetadataRetryPolicy.recordFailure(book, e);
        }
    }
}
