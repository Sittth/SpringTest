package spring.ru.springtest.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import spring.ru.springtest.api.AuthorsApi;
import spring.ru.springtest.dto.Author;
import spring.ru.springtest.services.AuthorService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AuthorController implements AuthorsApi {

    private final AuthorService authorService;

    @Override
    public ResponseEntity<Author> getAuthorById(UUID id) {
        return ResponseEntity.ok(authorService.findById(id));
    }

    @Override
    public ResponseEntity<Void> createAuthorById(Author author) {
        authorService.save(author);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> updateAuthorById(UUID id, Author author) {
        authorService.update(id, author);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deleteAuthorById(UUID id) {
        authorService.delete(id);

        return ResponseEntity.ok().build();
    }
}
