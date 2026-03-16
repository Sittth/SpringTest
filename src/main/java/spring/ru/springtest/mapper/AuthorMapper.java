package spring.ru.springtest.mapper;

import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.Author;
import spring.ru.springtest.models.AuthorModel;

@Component
public class AuthorMapper {

    public Author toDto(Author author) {
        if (author == null) {
            return null;
        }

        return new Author().id(author.getId()).name(author.getName());
    }

    public AuthorModel toEntity(Author dto) {
        if (dto == null) {
            return null;
        }

        AuthorModel authorModel = new AuthorModel();
        authorModel.setId(dto.getId());
        authorModel.setName(dto.getName());
        return authorModel;
    }
}
