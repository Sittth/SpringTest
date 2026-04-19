package spring.ru.springtest.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import spring.ru.springtest.models.CourseModel;

import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<CourseModel, UUID> {
    Page<CourseModel> findAllByIsDeletedFalse(Pageable pageable);

    Optional<CourseModel> findByIdAndIsDeletedFalse(UUID id);
}
