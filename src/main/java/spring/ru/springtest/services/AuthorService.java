package spring.ru.springtest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.springtest.dto.*;
import spring.ru.springtest.exceptions.AuthorNotFoundException;
import spring.ru.springtest.mapper.AuthorMapper;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.models.BookModel;
import spring.ru.springtest.repositories.AuthorRepository;
import spring.ru.springtest.repositories.BookRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public AuthorResponse findById(UUID id) {

        log.debug("Finding author by id: {}", id);

        AuthorModel authorModel = authorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Author not found with id {}", id);
                    return new AuthorNotFoundException("Author with id " + id + " not found");
                });

        return authorMapper.toResponse(authorModel);
    }

    @Transactional
    public AuthorResponse save(AuthorRequestCreate requestCreate) {

        log.debug("Saving author: {}", requestCreate);

        AuthorModel entity = authorMapper.toEntity(requestCreate);

        if (requestCreate.getBooks() != null) {
            List<BookModel> books = new ArrayList<>();

            for (BookRequestCreate book : requestCreate.getBooks()) {
                BookModel bookModel = new BookModel();
                bookModel.setTitle(book.getTitle());
                bookModel.setAuthor(entity);
                books.add(bookModel);
            }

            entity.setBooks(books);
        }

        AuthorModel saved = authorRepository.save(entity);

        log.info("Saved author with id {}", saved.getId());

        return authorMapper.toResponse(saved);
    }

    @Transactional
    public AuthorResponse update(UUID id, AuthorRequestUpdate requestUpdate) {

        log.debug("Update author with id: {}", id);

        AuthorModel existingAuthor = authorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Update failed: author not found with id {}", id);
                    return new AuthorNotFoundException("Author with id " + id + " not found");
                });

        authorMapper.updateEntityFromDto(requestUpdate, existingAuthor);

        if (requestUpdate.getBooks() != null) {
            List<BookModel> books = new ArrayList<>();

            for (BookRequestUpdate book : requestUpdate.getBooks()) {

                if (book.getId() != null) {
                    BookModel existing = bookRepository.findById(book.getId()).orElseThrow();

                    if (book.getTitle() != null) {
                        existing.setTitle(book.getTitle());
                    }
                    books.add(existing);
                } else {
                    BookModel bookModel = new BookModel();
                    bookModel.setTitle(book.getTitle());
                    bookModel.setAuthor(existingAuthor);
                    books.add(bookModel);
                }
            }
            existingAuthor.setBooks(books);
        }

        authorMapper.updateEntityFromDto(requestUpdate, existingAuthor);
        AuthorModel saved = authorRepository.save(existingAuthor);

        log.info("Updated author with id {}", id);

        return authorMapper.toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {

        log.debug("Delete author with id {}", id);

        AuthorModel authorModel = authorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.warn("Attempt to delete non-existent or already deleted author with id: {}", id);
                    return new AuthorNotFoundException("Author with id " + id + " not found");
                });

        if (authorModel.getBooks() != null) {
            authorModel.getBooks().forEach(bookModel -> bookModel.setDeleted(true));
        }

        authorModel.setDeleted(true);

        authorRepository.save(authorModel);

        log.info("Deleted author with id {}", id);
    }
}