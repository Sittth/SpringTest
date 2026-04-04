package spring.ru.springtest.mapper;

import org.mapstruct.*;
import spring.ru.springtest.dto.AuthorRequestCreate;
import spring.ru.springtest.dto.AuthorRequestUpdate;
import spring.ru.springtest.dto.AuthorResponse;
import spring.ru.springtest.models.AuthorModel;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = BookMapper.class)
public interface AuthorMapper {

    AuthorResponse toResponse(AuthorModel author);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    AuthorModel toEntity(AuthorRequestCreate requestCreate);

    @Mapping(target = "books", ignore = true)
    void updateEntityFromDto(AuthorRequestUpdate dto, @MappingTarget AuthorModel author);

    @AfterMapping
    default void linkBooksToAuthor(@MappingTarget AuthorModel authorModel) {
        if (authorModel.getBooks() != null) {
            authorModel.getBooks().forEach(book -> book.setAuthor(authorModel));
        }
    }
}