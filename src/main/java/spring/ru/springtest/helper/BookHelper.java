package spring.ru.springtest.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.BookRequestUpdate;
import spring.ru.springtest.exceptions.EntityNotFoundException;
import spring.ru.springtest.mapper.BookMapper;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.models.BookModel;
import spring.ru.springtest.repositories.BookRepository;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookHelper {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public List<BookModel> processBooks(List<BookRequestUpdate> bookDto, AuthorModel author) {
        if (bookDto == null) {
            return null;
        }

        List<BookModel> books = new ArrayList<>();
        for (BookRequestUpdate dto : bookDto) {
            if (dto.getId() != null) {
                BookModel existingBook = bookRepository.findById(dto.getId())
                        .orElseThrow(() -> new EntityNotFoundException("Book", dto.getId()));
                if (dto.getTitle() != null) {
                    existingBook.setTitle(dto.getTitle());
                }
                books.add(existingBook);
            } else {
                BookModel newBook = bookMapper.toEntity(dto);
                newBook.setAuthor(author);
                books.add(newBook);
            }
        }
        return books;
    }
}
