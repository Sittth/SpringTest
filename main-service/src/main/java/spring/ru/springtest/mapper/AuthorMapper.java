package spring.ru.springtest.mapper;

import org.mapstruct.*;
import org.springframework.data.domain.Page;
import spring.ru.springtest.dto.create.AuthorCreateRequest;
import spring.ru.springtest.dto.create.BookCreateRequest;
import spring.ru.springtest.dto.response.AuthorResponse;
import spring.ru.springtest.dto.response.BookResponse;
import spring.ru.springtest.dto.response.GetAuthors200Response;
import spring.ru.springtest.dto.update.AuthorUpdateRequest;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.models.BookModel;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthorMapper {

    AuthorResponse toResponse(AuthorModel author);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "books", source = "books")
    AuthorModel toEntity(AuthorCreateRequest requestCreate);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "books", source = "books")
    void updateEntityFromDto(AuthorUpdateRequest dto, @MappingTarget AuthorModel author);

    default BookResponse toBookResponse(BookModel bookModel) {
        if (bookModel == null) return null;

        BookResponse bookResponse = new BookResponse();
        bookResponse.setId(bookModel.getId());
        bookResponse.setTitle(bookModel.getTitle());

        return bookResponse;
    }

    default BookModel toBookEntity(BookCreateRequest requestCreate) {
        if (requestCreate == null) return null;

        BookModel bookModel = new BookModel();
        bookModel.setTitle(requestCreate.getTitle());
        bookModel.setPublisher(requestCreate.getPublisher());
        bookModel.setPrice(requestCreate.getPrice());

        return bookModel;
    }

    default GetAuthors200Response toPageResponse(Page<AuthorResponse> page) {
        return new GetAuthors200Response()
            .content(page.getContent())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements());
    }

    @AfterMapping
    default void linkBooks(@MappingTarget AuthorModel author) {
        if (author.getBooks() != null) {
            author.getBooks().forEach(book -> book.setAuthor(author));
        }
    }
}