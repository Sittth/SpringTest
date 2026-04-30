package spring.ru.springtest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import spring.ru.springtest.dto.create.BookCreateRequest;
import spring.ru.springtest.dto.response.BookResponse;
import spring.ru.springtest.dto.update.BookUpdateRequest;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.models.BookModel;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookMapper {

    BookResponse toResponse(BookModel book);

    List<BookResponse> toResponse(List<BookModel> books);

    BookModel toEntity(BookCreateRequest dto);

    BookModel toEntity(BookUpdateRequest dto);

    default BookModel toEntity(BookCreateRequest dto, AuthorModel author) {
        BookModel book = toEntity(dto);
        book.setAuthor(author);
        return book;
    }
}
