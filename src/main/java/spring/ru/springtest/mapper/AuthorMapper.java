package spring.ru.springtest.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import spring.ru.springtest.dto.AuthorRequestCreate;
import spring.ru.springtest.dto.AuthorRequestUpdate;
import spring.ru.springtest.dto.AuthorResponse;
import spring.ru.springtest.dto.BookRequestCreate;
import spring.ru.springtest.helper.BookHelper;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.models.BookModel;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = BookMapper.class)
public abstract class AuthorMapper {

    @Autowired
    protected BookMapper bookMapper;

    @Autowired
    protected BookHelper bookHelper;

    public abstract AuthorResponse toResponse(AuthorModel author);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    public abstract AuthorModel toEntity(AuthorRequestCreate requestCreate);

    @Mapping(target = "books", ignore = true)
    public abstract void updateEntityFromDto(AuthorRequestUpdate dto, @MappingTarget AuthorModel author);

    protected List<BookModel> mapBooksWithAuthor(List<BookRequestCreate> booksDto,
                                                 @MappingTarget AuthorModel author) {
        if (booksDto == null) {
            return null;
        }
        return booksDto.stream()
                .map(dto -> {
                    BookModel book = bookMapper.toEntity(dto);
                    book.setAuthor(author);
                    return book;
                })
                .collect(Collectors.toList());
    }

    @AfterMapping
    protected void createBooks(@MappingTarget AuthorModel author, AuthorRequestCreate requestCreate) {
        if (requestCreate.getBooks() != null) {
            List<BookModel> books = requestCreate.getBooks().stream()
                    .map(bookDto -> {
                        BookModel book = bookMapper.toEntity(bookDto);
                        book.setAuthor(author);
                        return book;
                    })
                    .collect(Collectors.toList());
            author.setBooks(books);
        }
    }

    @AfterMapping
    protected void updateBooks(@MappingTarget AuthorModel author, AuthorRequestUpdate requestUpdate) {
        List<BookModel> processedBooks = bookHelper.processBooks(requestUpdate.getBooks(), author);
        if (processedBooks != null) {
            author.setBooks(processedBooks);
        }
    }
}