package spring.ru.springtest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.springtest.dto.*;
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

    private BookModel processBook(BookRequestUpdate dto, AuthorModel author) {

        if (dto.getId() != null) {

            BookModel existing = author.getBooks().stream()
                    .filter(book -> book.getId().equals(dto.getId()))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Book", dto.getId()));

            if (dto.getTitle() != null) {
                existing.setTitle(dto.getTitle());
            }

            return existing;

        } else {
            BookModel newBook = bookMapper.toEntity(dto);
            newBook.setAuthor(author);
            return newBook;
        }
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

        log.debug("Finding author by id: {}", id);

        AuthorModel authorModel = findExistingAuthor(id);

        return authorMapper.toResponse(authorModel);
    }

    @Transactional
    public AuthorResponse save(AuthorRequestCreate requestCreate) {

        log.debug("Saving author: {}", requestCreate);

        AuthorModel entity = authorMapper.toEntity(requestCreate);
        AuthorModel saved = authorRepository.save(entity);

        log.info("Saved author with id {}", saved.getId());

        return authorMapper.toResponse(saved);
    }

    @Transactional
    public AuthorResponse update(UUID id, AuthorRequestUpdate requestUpdate) {

        log.debug("Update author with id: {}", id);

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

        log.debug("Delete author with id {}", id);

        AuthorModel authorModel = findExistingAuthor(id);

        if (authorModel.getBooks() != null) {
            authorModel.getBooks().forEach(bookModel -> bookModel.setDeleted(true));
        }

        authorModel.setDeleted(true);

        authorRepository.save(authorModel);

        log.info("Deleted author with id {}", id);
    }
}