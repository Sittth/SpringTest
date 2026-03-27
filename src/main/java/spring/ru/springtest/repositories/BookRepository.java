package spring.ru.springtest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.models.BookModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<BookModel, UUID> {
    List<BookModel> findAllByIsDeletedFalse();

    Optional<BookModel> findByIdAndIsDeletedFalse(UUID id);
}
