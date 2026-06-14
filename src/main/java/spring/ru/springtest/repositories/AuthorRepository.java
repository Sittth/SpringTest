package spring.ru.springtest.repositories;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.ru.springtest.models.AuthorModel;

import java.util.Optional;
import java.util.UUID;

public interface AuthorRepository extends JpaRepository<AuthorModel, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = "books")
    @Query("""
        select a
        from AuthorModel a
        where a.id = :id
        and a.isDeleted = false
    """)
    Optional<AuthorModel> findByIdAndIsDeletedFalseForUpdate(@Param("id") UUID id);

    Page<AuthorModel> findAllByIsDeletedFalse(Pageable pageable);

    @EntityGraph(attributePaths = "books")
    Optional<AuthorModel> findByIdAndIsDeletedFalse(UUID id);
}
