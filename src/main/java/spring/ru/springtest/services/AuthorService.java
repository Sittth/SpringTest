package spring.ru.springtest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.springtest.dto.create.AuthorCreateRequest;
import spring.ru.springtest.dto.create.BookCreateRequest;
import spring.ru.springtest.dto.response.AuthorResponse;
import spring.ru.springtest.dto.response.BookResponse;
import spring.ru.springtest.dto.update.AuthorUpdateRequest;
import spring.ru.springtest.dto.update.BookUpdateRequest;
import spring.ru.springtest.exceptions.EntityNotFoundException;
import spring.ru.springtest.mapper.AuthorMapper;
import spring.ru.springtest.mapper.BookMapper;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.models.BookModel;
import spring.ru.springtest.repositories.AuthorRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final BookMapper bookMapper;

    private BookModel processBook(BookUpdateRequest dto, AuthorModel author) {

        BookModel existing = author.getBooks().stream()
                .filter(book -> book.getId().equals(dto.getId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Book", dto.getId()));

        if (dto.getTitle() != null) {
            existing.setTitle(dto.getTitle());
        }

        return existing;
    }

    private AuthorModel findExistingAuthor(UUID id) {
        return authorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Author not found with id {}", id);
                    return new EntityNotFoundException("Author ", id);
                });
    }

    @Transactional(readOnly = true)
    public AuthorResponse findById(UUID id) {

        log.info("Finding author by id: {}", id);

        AuthorModel authorModel = findExistingAuthor(id);

        return authorMapper.toResponse(authorModel);
    }

    @Transactional(readOnly = true)
    public Page<AuthorResponse> findAll(int page, int size) {

        log.info("Fetching authors page {} with size {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));

        Page<AuthorModel> authorsPage = authorRepository.findAllByIsDeletedFalse(pageable);

        return authorsPage.map(authorMapper::toResponse);
    }

    @Transactional
    public BookResponse createBook(UUID authorId, BookCreateRequest request) {

        log.info("Creating book with id {}", authorId);

        AuthorModel author = findExistingAuthor(authorId);

        BookModel book = bookMapper.toEntity(request, author);

        author.getBooks().add(book);

        authorRepository.save(author);

        log.info("Created book with id: {} for author id: {}", book.getId(), authorId);

        return bookMapper.toResponse(book);
    }

    @Transactional
    public AuthorResponse save(AuthorCreateRequest requestCreate) {

        log.info("Saving author: {}", requestCreate);

        AuthorModel entity = authorMapper.toEntity(requestCreate);
        AuthorModel saved = authorRepository.save(entity);

        log.info("Saved author with id {}", saved.getId());

        return authorMapper.toResponse(saved);
    }

    @Transactional
    public AuthorResponse update(UUID id, AuthorUpdateRequest requestUpdate) {

        log.info("Update author with id: {}", id);

        AuthorModel existingAuthor = findExistingAuthor(id);

        authorMapper.updateEntityFromDto(requestUpdate, existingAuthor);

        if (requestUpdate.getBooks() != null) {

            List<BookModel> books = requestUpdate.getBooks().stream()
                    .map(dto -> processBook(dto, existingAuthor))
                    .toList();

            existingAuthor.getBooks().clear();
            for (BookModel book : books) {
                book.setAuthor(existingAuthor);
                existingAuthor.getBooks().add(book);
            }
        }

        AuthorModel saved = authorRepository.save(existingAuthor);

        log.info("Updated author with id {}", id);

        return authorMapper.toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {

        log.info("Delete author with id {}", id);

        AuthorModel authorModel = findExistingAuthor(id);

        if (authorModel.getBooks() != null) {
            authorModel.getBooks().forEach(bookModel -> bookModel.setDeleted(true));
        }

        authorModel.setDeleted(true);

        authorRepository.save(authorModel);

        log.info("Deleted author with id {}", id);
    }
}