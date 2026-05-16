package spring.ru.springtest.mapper;

import org.mapstruct.*;
import spring.ru.springtest.dto.create.AuthorCreateRequest;
import spring.ru.springtest.dto.create.BookCreateRequest;
import spring.ru.springtest.dto.response.AuthorResponse;
import spring.ru.springtest.dto.response.BookResponse;
import spring.ru.springtest.dto.update.AuthorUpdateRequest;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.models.BookModel;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthorMapper {

    AuthorResponse toResponse(AuthorModel author);

    List<BookResponse> toBookResponses(List<BookModel> books);

    BookResponse toBookResponse(BookModel book);

    AuthorModel toEntity(AuthorCreateRequest requestCreate);

    BookModel toBookEntity(BookCreateRequest requestCreate);

    void updateEntityFromDto(AuthorUpdateRequest dto, @MappingTarget AuthorModel author);

    @AfterMapping
    default void linkBooks(@MappingTarget AuthorModel author) {
        if (author.getBooks() != null) {
            author.getBooks().forEach(book -> book.setAuthor(author));
        }
    }
}