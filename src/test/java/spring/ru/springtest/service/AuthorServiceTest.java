package spring.ru.springtest.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import spring.ru.springtest.client.BookMetadataResilientClient;
import spring.ru.springtest.client.metadata.dto.BookMetadataResponse;
import spring.ru.springtest.config.RedisConfig;
import spring.ru.springtest.controller.AbstractControllerTest;
import spring.ru.springtest.dto.create.AuthorCreateRequest;
import spring.ru.springtest.dto.create.BookCreateRequest;
import spring.ru.springtest.dto.response.AuthorResponse;
import spring.ru.springtest.dto.update.AuthorUpdateRequest;
import spring.ru.springtest.exceptions.EntityNotFoundException;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.repositories.AuthorRepository;
import spring.ru.springtest.repositories.CacheInvalidationQueueRepository;
import spring.ru.springtest.services.AuthorService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthorServiceTest extends AbstractControllerTest {

    @Autowired
    AuthorService authorService;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    CacheInvalidationQueueRepository cacheInvalidationQueueRepository;

    @MockitoBean
    BookMetadataResilientClient bookMetadataResilientClient;

    @Test
    void save_shouldPersistAuthorWithBooks_andRegisterMetadataForEachBook() {
        when(bookMetadataResilientClient.createWithResilience(any(UUID.class), any(String.class), any(BigDecimal.class)))
                .thenReturn(new BookMetadataResponse().publisher("Secker & Warburg").price(BigDecimal.valueOf(12.99)));

        AuthorCreateRequest request = new AuthorCreateRequest()
                .name("George Orwell")
                .books(List.of(new BookCreateRequest("1984", "Secker & Warburg", BigDecimal.valueOf(12.99))));

        AuthorResponse response = authorService.save(request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo("George Orwell");

        AuthorModel persisted = authorRepository.findByIdAndIsDeletedFalse(response.getId()).orElseThrow();
        assertThat(persisted.getBooks()).hasSize(1);
        assertThat(persisted.getBooks().get(0).getPublisher()).isEqualTo("Secker & Warburg");
    }

    @Test
    void findById_shouldReturnAuthor_whenExists() {
        when(bookMetadataResilientClient.createWithResilience(any(UUID.class), any(String.class), any(BigDecimal.class)))
                .thenReturn(new BookMetadataResponse().publisher("Some Publisher").price(BigDecimal.TEN));

        AuthorResponse created = authorService.save(new AuthorCreateRequest()
                .name("Jane Austen")
                .books(List.of(new BookCreateRequest("Some Book", "Some Publisher", BigDecimal.TEN))));

        AuthorResponse found = authorService.findById(created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getName()).isEqualTo("Jane Austen");
    }

    @Test
    void findById_shouldThrow_whenAuthorNotFound() {
        assertThatThrownBy(() -> authorService.findById(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void update_shouldChangeAuthorName() {
        when(bookMetadataResilientClient.createWithResilience(any(UUID.class), any(String.class), any(BigDecimal.class)))
                .thenReturn(new BookMetadataResponse().publisher("Publisher").price(BigDecimal.ONE));

        AuthorResponse created = authorService.save(new AuthorCreateRequest()
                .name("Old Name")
                .books(List.of(new BookCreateRequest("Book", "Publisher", BigDecimal.ONE))));

        AuthorResponse updated = authorService.update(created.getId(), new AuthorUpdateRequest().name("New Name"));

        assertThat(updated.getName()).isEqualTo("New Name");

        AuthorModel persisted = authorRepository.findByIdAndIsDeletedFalse(created.getId()).orElseThrow();
        assertThat(persisted.getName()).isEqualTo("New Name");
    }

    @Test
    void delete_shouldSoftDeleteAuthor_andQueueCacheInvalidation() {
        when(bookMetadataResilientClient.createWithResilience(any(UUID.class), any(String.class), any(BigDecimal.class)))
                .thenReturn(new BookMetadataResponse().publisher("Publisher").price(BigDecimal.ONE));

        AuthorResponse created = authorService.save(new AuthorCreateRequest()
                .name("To Delete")
                .books(List.of(new BookCreateRequest("Book", "Publisher", BigDecimal.ONE))));

        authorService.delete(created.getId());

        assertThat(authorRepository.findByIdAndIsDeletedFalse(created.getId())).isEmpty();

        boolean queued = cacheInvalidationQueueRepository.findAll().stream()
                .anyMatch(entry -> entry.getCacheName().equals(RedisConfig.AUTHOR_CACHE)
                        && entry.getCacheKey().equals(created.getId().toString()));
        assertThat(queued).isTrue();
    }
}
