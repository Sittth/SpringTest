package spring.ru.springtest.repositories;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.models.StudentModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<StudentModel, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select s
        from StudentModel s
        where s.id = :id
        and s.isDeleted = false
    """)
    Optional<StudentModel> findByIdAndIsDeletedFalseForUpdate(@Param("id") UUID id);

    List<StudentModel> findAllByIsDeletedFalse();

    Optional<StudentModel> findByIdAndIsDeletedFalse(UUID id);
}
