package spring.ru.springtest.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.BookRequestCreate;
import spring.ru.springtest.dto.BookRequestUpdate;
import spring.ru.springtest.dto.BookResponse;
import spring.ru.springtest.models.BookModel;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-14T16:20:42+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.1.jar, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class BookMapperImpl implements BookMapper {

    @Override
    public BookResponse toResponse(BookModel book) {
        if ( book == null ) {
            return null;
        }

        BookResponse bookResponse = new BookResponse();

        bookResponse.setId( book.getId() );
        bookResponse.setTitle( book.getTitle() );

        return bookResponse;
    }

    @Override
    public List<BookResponse> toResponse(List<BookModel> books) {
        if ( books == null ) {
            return null;
        }

        List<BookResponse> list = new ArrayList<BookResponse>( books.size() );
        for ( BookModel bookModel : books ) {
            list.add( toResponse( bookModel ) );
        }

        return list;
    }

    @Override
    public BookModel toEntity(BookRequestCreate dto) {
        if ( dto == null ) {
            return null;
        }

        BookModel bookModel = new BookModel();

        bookModel.setTitle( dto.getTitle() );

        return bookModel;
    }

    @Override
    public BookModel toEntity(BookRequestUpdate dto) {
        if ( dto == null ) {
            return null;
        }

        BookModel bookModel = new BookModel();

        bookModel.setTitle( dto.getTitle() );

        return bookModel;
    }
}
