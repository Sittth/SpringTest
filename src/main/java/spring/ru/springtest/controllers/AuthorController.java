package spring.ru.springtest.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import spring.ru.springtest.api.AuthorsApi;
import spring.ru.springtest.dto.create.AuthorCreateRequest;
import spring.ru.springtest.dto.response.AuthorResponse;
import spring.ru.springtest.dto.update.AuthorUpdateRequest;
import spring.ru.springtest.dto.response.GetAuthors200Response;
import spring.ru.springtest.services.AuthorService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
public class AuthorController implements AuthorsApi {

    private final AuthorService authorService;

    @Override
    public ResponseEntity<AuthorResponse> getAuthorById(UUID id) {
        return ResponseEntity.ok(authorService.findById(id));
    }

    @Override
    public ResponseEntity<GetAuthors200Response> getAuthors(
            @Min(0) @NotNull Integer page,
            @Min(1) @Max(50) @NotNull Integer size) {

        Page<AuthorResponse> result = authorService.findAll(page, size);

        GetAuthors200Response response = new GetAuthors200Response()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AuthorResponse> createAuthor(@Valid AuthorCreateRequest requestCreate) {
        AuthorResponse created = authorService.save(requestCreate);

        return ResponseEntity.status(201).body(created);
    }

    @Override
    public ResponseEntity<AuthorResponse> updateAuthorById(UUID id, @Valid AuthorUpdateRequest requestUpdate) {
        AuthorResponse updated = authorService.update(id, requestUpdate);

        return ResponseEntity.ok(updated);
    }

    @Override
    public ResponseEntity<Void> deleteAuthorById(UUID id) {
        authorService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
