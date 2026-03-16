package spring.ru.springtest.mapper;

import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.Book;
import spring.ru.springtest.models.BookModel;

@Component
public class BookMapper {

    public Book toDto(Book book) {
        if (book == null) {
            return null;
        }

        return new Book().id(book.getId()).title(book.getTitle());
    }

    public BookModel toEntity(Book dto) {
        if (dto == null) {
            return null;
        }

        BookModel bookModel = new BookModel();
        bookModel.setId(dto.getId());
        bookModel.setTitle(dto.getTitle());
        return bookModel;
    }
}
