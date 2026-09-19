package spring.ru.springtest.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import spring.ru.springtest.api.AuthorsApi;
import spring.ru.springtest.dto.create.AuthorCreateRequest;
import spring.ru.springtest.dto.response.AuthorResponse;
import spring.ru.springtest.dto.update.AuthorUpdateRequest;
import spring.ru.springtest.dto.response.GetAuthors200Response;
import spring.ru.springtest.mapper.AuthorMapper;
import spring.ru.springtest.services.AuthorService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AuthorController implements AuthorsApi {

    private final AuthorService authorService;
    private final AuthorMapper authorMapper;

    @Override
    public AuthorResponse getAuthorById(UUID id) {

        return authorService.findById(id);
    }

    @Override
    public GetAuthors200Response getAuthors(Integer page, Integer size) {

        return authorMapper.toPageResponse(authorService.findAllPaginated(page, size));
    }

    @Override
    public AuthorResponse createAuthor(AuthorCreateRequest requestCreate) {

        return authorService.save(requestCreate);
    }

    @Override
    public AuthorResponse updateAuthorById(UUID id, AuthorUpdateRequest requestUpdate) {

        return authorService.update(id, requestUpdate);
    }

    @Override
    public void deleteAuthorById(UUID id) {

        authorService.delete(id);
    }
}
