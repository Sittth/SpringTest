package spring.ru.springtest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.repositories.AuthorRepository;

import java.util.UUID;

@Service
@Transactional
public class AuthorService {

    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
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

    public void update(UUID id, AuthorModel updatedAuthor) {
        AuthorModel existingAuthor = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        existingAuthor.setName(updatedAuthor.getName());

        existingAuthor.getBooks().clear();

        if (updatedAuthor.getBooks() != null) {
            updatedAuthor.getBooks().forEach(book -> {
                book.setAuthor(existingAuthor);
                existingAuthor.getBooks().add(book);
            });
        }

        authorRepository.save(existingAuthor);
    }

    public void delete(UUID id) {
        authorRepository.deleteById(id);
    }
}
