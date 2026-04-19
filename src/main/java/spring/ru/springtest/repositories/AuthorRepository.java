package spring.ru.springtest.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import spring.ru.springtest.models.AuthorModel;

import java.util.Optional;
import java.util.UUID;

public interface AuthorRepository extends JpaRepository<AuthorModel, UUID> {
    Page<AuthorModel> findAllByIsDeletedFalse(Pageable pageable);

    @EntityGraph(attributePaths = "books")
    Optional<AuthorModel> findByIdAndIsDeletedFalse(UUID id);
}
