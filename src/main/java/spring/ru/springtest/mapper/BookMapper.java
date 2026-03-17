package spring.ru.springtest.mapper;

import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.Book;
import spring.ru.springtest.models.BookModel;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookMapper {

    public Book toDto(BookModel books) {
        if (books == null) {
            return null;
        }

        return new Book()
                .id(books.getId())
                .title(books.getTitle());
    }

    public BookModel toEntity(Book dto) {
        if (dto == null) {
            return null;
        }

        BookModel model = new BookModel();
        model.setId(dto.getId());
        model.setTitle(dto.getTitle());

        return model;
    }

    public List<Book> toDto(List<BookModel> books) {
        if (books == null) {
            return null;
        }

        return books.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<BookModel> toEntity(List<Book> dto) {
        if (dto == null) {
            return null;
        }

        return dto.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
