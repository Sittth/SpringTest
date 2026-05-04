package spring.ru.springtest.mapper;

import org.mapstruct.*;
import spring.ru.springtest.dto.create.AuthorCreateRequest;
import spring.ru.springtest.dto.response.AuthorResponse;
import spring.ru.springtest.dto.update.AuthorUpdateRequest;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.models.BookModel;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = BookMapper.class)
public interface AuthorMapper {

    AuthorResponse toResponse(AuthorModel author);

    AuthorModel toEntity(AuthorCreateRequest requestCreate);

    void updateEntityFromDto(AuthorUpdateRequest dto, @MappingTarget AuthorModel author);

    @AfterMapping
    default void linkBooks(@MappingTarget AuthorModel author) {
        if (author.getBooks() != null) {
            author.getBooks().forEach(book -> book.setAuthor(author));
        }
    }
}