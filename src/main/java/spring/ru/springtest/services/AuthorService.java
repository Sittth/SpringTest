package spring.ru.springtest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.springtest.dto.Author;
import spring.ru.springtest.exceptions.AuthorNotFoundException;
import spring.ru.springtest.mapper.AuthorMapper;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.repositories.AuthorRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Transactional(readOnly = true)
    public Author findById(UUID id) {
        log.debug("Finding author by id: {}", id);
        AuthorModel authorModel = authorRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Author not found with id {}", id);
                    return new AuthorNotFoundException("Author with id " + id + " not found");
                });

        return authorMapper.toDto(authorModel);
    }

    @Transactional
    public void save(Author authorDto) {
        log.debug("Saving author: {}", authorDto);
        AuthorModel authorModel = authorMapper.toEntity(authorDto);

        authorRepository.save(authorModel);
        log.info("Saved author with id {}", authorModel.getId());
    }

    @Transactional
    public void update(UUID id, Author dto) {
        log.debug("Update author with id: {}", id);
        AuthorModel existingAuthor = authorRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Update failed: author not found with id {}", id);
                    return new AuthorNotFoundException("Author with id " + id + " not found");
                });

        authorMapper.updateEntityFromDto(dto, existingAuthor);
        authorRepository.save(existingAuthor);
        log.info("Updated author with id {}", id);
    }

    @Transactional
    public void delete(UUID id) {

        log.debug("Delete author with id {}", id);
        if (!authorRepository.existsById(id)) {
            log.warn("Attempt to delete non-existent author with id: {}", id);
            throw new AuthorNotFoundException("Author with id " + id + " not found");
        }
        authorRepository.deleteById(id);
        log.info("Deleted author with id {}", id);
    }
}