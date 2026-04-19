package spring.ru.springtest.mapper;

import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.AuthorRequestCreate;
import spring.ru.springtest.dto.AuthorRequestUpdate;
import spring.ru.springtest.dto.AuthorResponse;
import spring.ru.springtest.models.AuthorModel;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-14T16:20:42+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.1.jar, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class AuthorMapperImpl implements AuthorMapper {

    @Autowired
    private BookMapper bookMapper;

    @Override
    public AuthorResponse toResponse(AuthorModel author) {
        if ( author == null ) {
            return null;
        }

        AuthorResponse authorResponse = new AuthorResponse();

        authorResponse.setId( author.getId() );
        authorResponse.setName( author.getName() );
        authorResponse.setBooks( bookMapper.toResponse( author.getBooks() ) );

        return authorResponse;
    }

    @Override
    public AuthorModel toEntity(AuthorRequestCreate requestCreate) {
        if ( requestCreate == null ) {
            return null;
        }

        AuthorModel authorModel = new AuthorModel();

        authorModel.setName( requestCreate.getName() );

        return authorModel;
    }

    @Override
    public void updateEntityFromDto(AuthorRequestUpdate dto, AuthorModel author) {
        if ( dto == null ) {
            return;
        }

        author.setName( dto.getName() );
    }
}
