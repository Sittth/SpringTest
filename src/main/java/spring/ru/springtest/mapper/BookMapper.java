package spring.ru.springtest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import spring.ru.springtest.dto.Book;
import spring.ru.springtest.models.BookModel;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {

    Book toDto(BookModel book);

    @Mapping(target = "author", ignore = true)
    BookModel toEntity(Book dto);

    List<Book> toDto(List<BookModel> books);
    List<BookModel> toEntity(List<Book> dto);
}
