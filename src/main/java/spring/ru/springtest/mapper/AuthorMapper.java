package spring.ru.springtest.mapper;

import jakarta.persistence.EntityNotFoundException;
import org.mapstruct.*;
import org.springframework.data.domain.Page;
import spring.ru.springtest.client.metadata.dto.BookMetadataResponse;
import spring.ru.springtest.dto.create.AuthorCreateRequest;
import spring.ru.springtest.dto.create.BookCreateRequest;
import spring.ru.springtest.dto.response.AuthorResponse;
import spring.ru.springtest.dto.response.BookResponse;
import spring.ru.springtest.dto.response.GetAuthors200Response;
import spring.ru.springtest.dto.update.AuthorUpdateRequest;
import spring.ru.springtest.dto.update.BookUpdateRequest;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.models.BookModel;
import spring.ru.springtest.models.enums.BookMetadataStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    @Mapping(target = "books", ignore = true)
    void updateEntityFromDto(AuthorUpdateRequest dto, @MappingTarget AuthorModel author);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "metadataStatus", ignore = true)
    @Mapping(target = "title", ignore = true)
    void updateBookMetadata(BookMetadataResponse source, @MappingTarget BookModel target);

    default BookResponse toBookResponse(BookModel bookModel) {
        if (bookModel == null) return null;

        BookResponse bookResponse = new BookResponse();
        bookResponse.setId(bookModel.getId());
        bookResponse.setTitle(bookModel.getTitle());
        bookResponse.setPublisher(bookModel.getPublisher());
        bookResponse.setPrice(bookModel.getPrice());

        return bookResponse;
    }

    default BookModel toBookEntity(BookCreateRequest requestCreate) {
        if (requestCreate == null) return null;

        BookModel bookModel = new BookModel();
        bookModel.setTitle(requestCreate.getTitle());
        bookModel.setPublisher(requestCreate.getPublisher());
        bookModel.setPrice(requestCreate.getPrice());
        bookModel.setMetadataStatus(BookMetadataStatus.PENDING);

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

    @AfterMapping
    default void mergeBooks(AuthorUpdateRequest dto, @MappingTarget AuthorModel author) {

        List<BookUpdateRequest> requestedBooks = dto.getBooks() == null ? List.of() : dto.getBooks();

        Map<UUID, BookModel> existingById = author.getBooks() == null
                ? Map.of()
                : author.getBooks().stream()
                .collect(Collectors.toMap(BookModel::getId, Function.identity()));

        List<BookModel> mergedBooks = new ArrayList<>();

        for (BookUpdateRequest bookUpdate : requestedBooks) {
            if (bookUpdate.getId() == null) {
                throw new IllegalArgumentException(
                        "Book id is required to update an existing book; creating books is only supported via author/book creation");
            }

            BookModel existing = existingById.get(bookUpdate.getId());
            if (existing == null) {
                throw new EntityNotFoundException(
                        "Book " + bookUpdate.getId() + " not found for author " + author.getId());
            }

            if (bookUpdate.getTitle() != null) {
                existing.setTitle(bookUpdate.getTitle());
            }

            mergedBooks.add(existing);
        }

        if (author.getBooks() == null) {
            author.setBooks(mergedBooks);
        } else {
            author.getBooks().clear();
            author.getBooks().addAll(mergedBooks);
        }
    }
}