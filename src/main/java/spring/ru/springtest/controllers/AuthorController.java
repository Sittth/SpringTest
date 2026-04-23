package spring.ru.springtest.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import spring.ru.springtest.api.AuthorsApi;
import spring.ru.springtest.dto.*;
import spring.ru.springtest.services.AuthorService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AuthorController implements AuthorsApi {

    private final AuthorService authorService;

    @Override
    public ResponseEntity<AuthorResponse> getAuthorById(UUID id) {
        return ResponseEntity.ok(authorService.findById(id));
    }

    @Override
    public ResponseEntity<AuthorResponse> createAuthor(@Valid AuthorRequestCreate requestCreate) {
        AuthorResponse created = authorService.save(requestCreate);

        return ResponseEntity.status(201).body(created);
    }

    @Override
    public ResponseEntity<BookResponse> createBookForAuthor(
            @PathVariable("id") UUID id,
            @Valid @RequestBody BookRequestCreate requestCreate) {
        BookResponse created = authorService.createBook(id, requestCreate);

        return ResponseEntity.status(201).body(created);
    }

    @Override
    public ResponseEntity<AuthorResponse> updateAuthorById(UUID id, @Valid AuthorRequestUpdate requestUpdate) {
        AuthorResponse updated = authorService.update(id, requestUpdate);

        return ResponseEntity.ok(updated);
    }

    @Override
    public ResponseEntity<Void> deleteAuthorById(UUID id) {
        authorService.delete(id);

        return ResponseEntity.ok().build();
    }
}
