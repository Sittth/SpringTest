package spring.ru.springtest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import spring.ru.springtest.dto.BookRequestCreate;
import spring.ru.springtest.dto.BookRequestUpdate;
import spring.ru.springtest.dto.BookResponse;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.models.BookModel;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookMapper {

    BookResponse toResponse(BookModel book);

    List<BookResponse> toResponse(List<BookModel> books);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    BookModel toEntity(BookRequestCreate dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    BookModel toEntity(BookRequestUpdate dto);

    default BookModel toEntity(BookRequestCreate dto, AuthorModel author) {
        BookModel book = toEntity(dto);
        book.setAuthor(author);
        return book;
    }
}
