package spring.ru.springtest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.springtest.dto.*;
import spring.ru.springtest.exceptions.EntityNotFoundException;
import spring.ru.springtest.mapper.AuthorMapper;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.repositories.AuthorRepository;
import spring.ru.springtest.repositories.BookRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final BookRepository bookRepository;

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