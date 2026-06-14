package spring.ru.springtest.repositories;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.models.CourseModel;

import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<CourseModel, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select c
        from CourseModel c
        where c.id = :id
        and c.isDeleted = false
    """)
    Optional<CourseModel> findByIdAndIsDeletedFalseForUpdate(@Param("id") UUID id);

    Page<CourseModel> findAllByIsDeletedFalse(Pageable pageable);

    Optional<CourseModel> findByIdAndIsDeletedFalse(UUID id);
}
