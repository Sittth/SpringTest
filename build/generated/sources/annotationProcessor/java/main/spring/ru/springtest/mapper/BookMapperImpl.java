package spring.ru.springtest.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.BookRequestCreate;
import spring.ru.springtest.dto.BookResponse;
import spring.ru.springtest.models.BookModel;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-03T22:08:53+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.1.jar, environment: Java 25 (Oracle Corporation)"
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
}
