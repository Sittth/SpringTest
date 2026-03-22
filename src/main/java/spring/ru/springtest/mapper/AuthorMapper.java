package spring.ru.springtest.mapper;

import org.mapstruct.*;
import spring.ru.springtest.dto.Author;
import spring.ru.springtest.models.AuthorModel;

@Mapper(componentModel = "spring", uses = BookMapper.class)
public interface AuthorMapper {

    Author toDto(AuthorModel author);

    AuthorModel toEntity(Author dto);

    void updateEntityFromDto(Author dto, @MappingTarget AuthorModel author);

    @AfterMapping
    default void linkBooksToAuthor(@MappingTarget AuthorModel authorModel) {
        if (authorModel.getBooks() != null) {
            authorModel.getBooks().forEach(book -> book.setAuthor(authorModel));
        }
    }
}