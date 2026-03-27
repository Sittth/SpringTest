package spring.ru.springtest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.models.StudentModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<StudentModel, UUID> {
    List<StudentModel> findAllByIsDeletedFalse();

    Optional<StudentModel> findByIdAndIsDeletedFalse(UUID id);
}
