package spring.ru.springtest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.models.CourseModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<CourseModel, UUID> {
    List<CourseModel> findAllByIsDeletedFalse();

    Optional<CourseModel> findByIdAndIsDeletedFalse(UUID id);
}
