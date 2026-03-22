package spring.ru.springtest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.springtest.dto.Author;
import spring.ru.springtest.mapper.AuthorMapper;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.repositories.AuthorRepository;

import java.util.UUID;

@Service
@Transactional
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Autowired
    public AuthorService(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    @Transactional(readOnly = true)
    public AuthorModel findById(UUID id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));
    }

    public void save(AuthorModel author) {
        if (author.getBooks() != null) {
            author.getBooks().forEach(book -> book.setAuthor(author));
        }

        authorRepository.save(author);
    }

    public void update(UUID id, Author dto) {
        AuthorModel existingAuthor = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        authorMapper.updateEntityFromDto(dto, existingAuthor);

        if (existingAuthor.getBooks() != null) {
            existingAuthor.getBooks().forEach(book -> book.setAuthor(existingAuthor));
        }

        authorRepository.save(existingAuthor);
    }

    public void delete(UUID id) {
        authorRepository.deleteById(id);
    }
}