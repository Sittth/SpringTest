package spring.ru.springtest.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.Book;
import spring.ru.springtest.models.BookModel;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-24T18:29:47+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.1.jar, environment: Java 25 (Oracle Corporation)"
)
@Component
public class BookMapperImpl implements BookMapper {

    @Override
    public Book toDto(BookModel book) {
        if ( book == null ) {
            return null;
        }

        Book book1 = new Book();

        book1.setId( book.getId() );
        book1.setTitle( book.getTitle() );

        return book1;
    }

    @Override
    public BookModel toEntity(Book dto) {
        if ( dto == null ) {
            return null;
        }

        BookModel bookModel = new BookModel();

        bookModel.setId( dto.getId() );
        bookModel.setTitle( dto.getTitle() );

        return bookModel;
    }

    @Override
    public List<Book> toDto(List<BookModel> books) {
        if ( books == null ) {
            return null;
        }

        List<Book> list = new ArrayList<Book>( books.size() );
        for ( BookModel bookModel : books ) {
            list.add( toDto( bookModel ) );
        }

        return list;
    }

    @Override
    public List<BookModel> toEntity(List<Book> dto) {
        if ( dto == null ) {
            return null;
        }

        List<BookModel> list = new ArrayList<BookModel>( dto.size() );
        for ( Book book : dto ) {
            list.add( toEntity( book ) );
        }

        return list;
    }
}
