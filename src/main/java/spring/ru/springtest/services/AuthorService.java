package spring.ru.springtest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import spring.ru.springtest.client.BookMetadataResilientClient;
import spring.ru.springtest.client.metadata.dto.BookMetadataResponse;
import spring.ru.springtest.config.RedisConfig;
import spring.ru.springtest.dto.create.AuthorCreateRequest;
import spring.ru.springtest.dto.response.AuthorResponse;
import spring.ru.springtest.dto.update.AuthorUpdateRequest;
import spring.ru.springtest.exceptions.BookMetadataRegistrationException;
import spring.ru.springtest.exceptions.EntityNotFoundException;
import spring.ru.springtest.mapper.AuthorMapper;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.models.BookModel;
import spring.ru.springtest.models.enums.BookMetadataStatus;
import spring.ru.springtest.repositories.AuthorRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final BookMetadataResilientClient bookMetadataResilientClient;
    private final TransactionTemplate transactionTemplate;

    private AuthorModel findExistingAuthor(UUID id) {
        return authorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Author not found with id {}", id);
                    return new EntityNotFoundException("Author " + id);
                });
    }

    private AuthorModel findExistingAuthorForUpdate(UUID id) {
        return authorRepository.findByIdAndIsDeletedFalseForUpdate(id)
                .orElseThrow(() -> {
                    log.error("Author not found with id {}", id);
                    return new EntityNotFoundException("Author " + id);
                });
    }

    @Cacheable(value = RedisConfig.AUTHOR_CACHE, key = "#id")
    @Transactional(readOnly = true)
    public AuthorResponse findById(UUID id) {

        log.info("Finding author by id: {}", id);

        AuthorModel authorModel = findExistingAuthor(id);

        return authorMapper.toResponse(authorModel);
    }

    @Transactional(readOnly = true)
    public Page<AuthorResponse> findAllPaginated(int page, int size) {

        log.info("Fetching authors page {} with size {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));

        Page<AuthorModel> authorsPage = authorRepository.findAllByIsDeletedFalse(pageable);

        return authorsPage.map(authorMapper::toResponse);
    }

    public AuthorResponse save(AuthorCreateRequest requestCreate) {

        log.info("Saving author: {}", requestCreate);

        AuthorModel saved = transactionTemplate.execute(status -> {
            AuthorModel entity = authorMapper.toEntity(requestCreate);
            return authorRepository.save(entity);
        });

        saved.getBooks().forEach(this::enrichNewBook);

        transactionTemplate.executeWithoutResult(status -> authorRepository.save(saved));

        return authorMapper.toResponse(saved);
    }

    private void enrichNewBook(BookModel book) {
        try {
            BookMetadataResponse meta = bookMetadataResilientClient.createWithResilience(
                    book.getId(),
                    book.getPublisher(),
                    book.getPrice());

            authorMapper.updateBookMetadata(meta, book);
            book.setMetadataStatus(BookMetadataStatus.CONFIRMED);
        } catch (BookMetadataRegistrationException e) {
            log.warn("Book metadata registration failed for book {}, leaving status PENDING for later retry", book.getId(), e);
        }
    }

    @CachePut(value = RedisConfig.AUTHOR_CACHE, key = "#id")
    @Transactional
    public AuthorResponse update(UUID id, AuthorUpdateRequest requestUpdate) {

        log.info("Update author with id: {}", id);

        AuthorModel existingAuthor = findExistingAuthorForUpdate(id);

        authorMapper.updateEntityFromDto(requestUpdate, existingAuthor);

        log.info("Updated author with id {}", id);

        return authorMapper.toResponse(existingAuthor);
    }

    @CacheEvict(value = RedisConfig.AUTHOR_CACHE, key = "#id")
    @Transactional
    public void delete(UUID id) {

        log.info("Delete author with id {}", id);

        AuthorModel authorModel = findExistingAuthorForUpdate(id);

        authorRepository.delete(authorModel);

        log.info("Deleted author with id {}", id);
    }
}