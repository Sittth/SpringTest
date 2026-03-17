package spring.ru.springtest.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.Author;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.models.BookModel;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthorMapper {

    private final BookMapper bookMapper;

    public Author toDto(AuthorModel author) {
        if (author == null) {
            return null;
        }

        return new Author().id(author.getId()).name(author.getName())
                .books(bookMapper.toDto(author.getBooks()));
    }

    public AuthorModel toEntity(Author dto) {
        if (dto == null) {
            return null;
        }

        AuthorModel authorModel = new AuthorModel();
        authorModel.setId(dto.getId());
        authorModel.setName(dto.getName());

        List<BookModel> books = bookMapper.toEntity(dto.getBooks());

        if (books != null) {
            books.forEach(book -> {
                book.setAuthor(authorModel);
            });
        }

        authorModel.setBooks(books);
        return authorModel;
    }
}
