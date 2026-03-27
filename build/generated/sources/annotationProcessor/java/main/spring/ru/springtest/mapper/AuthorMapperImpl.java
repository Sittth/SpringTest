package spring.ru.springtest.mapper;

import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.Author;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.models.BookModel;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-27T20:27:18+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.1.jar, environment: Java 25 (Oracle Corporation)"
)
@Component
public class AuthorMapperImpl implements AuthorMapper {

    @Autowired
    private BookMapper bookMapper;

    @Override
    public Author toDto(AuthorModel author) {
        if ( author == null ) {
            return null;
        }

        Author author1 = new Author();

        author1.setId( author.getId() );
        author1.setName( author.getName() );
        author1.setBooks( bookMapper.toDto( author.getBooks() ) );

        return author1;
    }

    @Override
    public AuthorModel toEntity(Author dto) {
        if ( dto == null ) {
            return null;
        }

        AuthorModel authorModel = new AuthorModel();

        authorModel.setId( dto.getId() );
        authorModel.setName( dto.getName() );
        authorModel.setBooks( bookMapper.toEntity( dto.getBooks() ) );

        linkBooksToAuthor( authorModel );

        return authorModel;
    }

    @Override
    public void updateEntityFromDto(Author dto, AuthorModel author) {
        if ( dto == null ) {
            return;
        }

        author.setId( dto.getId() );
        author.setName( dto.getName() );
        if ( author.getBooks() != null ) {
            List<BookModel> list = bookMapper.toEntity( dto.getBooks() );
            if ( list != null ) {
                author.getBooks().clear();
                author.getBooks().addAll( list );
            }
            else {
                author.setBooks( null );
            }
        }
        else {
            List<BookModel> list = bookMapper.toEntity( dto.getBooks() );
            if ( list != null ) {
                author.setBooks( list );
            }
        }

        linkBooksToAuthor( author );
    }
}
