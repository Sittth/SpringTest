package spring.ru.springtest.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import spring.ru.springtest.api.AuthorsApi;
import spring.ru.springtest.dto.Author;
import spring.ru.springtest.mapper.AuthorMapper;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.services.AuthorService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AuthorController implements AuthorsApi {

    private final AuthorService authorService;
    private final AuthorMapper authorMapper;

    @Override
    public ResponseEntity<Author> getAuthorById(UUID id) {
        AuthorModel model = authorService.findById(id);

        Author dto = authorMapper.toDto(model);

        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<Void> createAuthorById(Author author) {
        AuthorModel model = authorMapper.toEntity(author);

        authorService.save(model);

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
