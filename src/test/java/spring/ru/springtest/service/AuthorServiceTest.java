package spring.ru.springtest.service;

import spring.ru.springtest.client.BookMetadataResilientClient;
import spring.ru.springtest.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spring.ru.springtest.dto.response.AuthorResponse;
import spring.ru.springtest.mapper.AuthorMapper;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.repositories.AuthorRepository;
import spring.ru.springtest.services.AuthorService;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private AuthorMapper authorMapper;
    @Mock
    private BookMetadataResilientClient bookMetadataResilientClient;

    @InjectMocks
    private AuthorService authorService;

    @Test
    void findById_shouldReturnAuthor_whenAuthorExists() {
        UUID id = UUID.randomUUID();
        AuthorModel model = new AuthorModel();
        model.setId(id);
        model.setBooks(java.util.List.of());

        AuthorResponse expected = new AuthorResponse().id(id);

        when(authorRepository.findByIdAndIsDeletedFalse(id)).thenReturn(Optional.of(model));
        when(authorMapper.toResponse(model)).thenReturn(expected);

        AuthorResponse actual = authorService.findById(id);

        assertThat(actual).isEqualTo(expected);
        verifyNoInteractions(bookMetadataResilientClient);
    }

    @Test
    void findById_shouldThrow_whenAuthorNotFound() {
        UUID id = UUID.randomUUID();
        when(authorRepository.findByIdAndIsDeletedFalse(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.findById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(id.toString());

        verifyNoInteractions(authorMapper);
    }

    @Test
    void delete_shouldEvictCacheAndRemoveAuthor() {
        UUID id = UUID.randomUUID();
        AuthorModel model = new AuthorModel();
        model.setId(id);

        when(authorRepository.findByIdAndIsDeletedFalseForUpdate(id)).thenReturn(Optional.of(model));

        authorService.delete(id);

        verify(authorRepository).delete(model);
    }
}
