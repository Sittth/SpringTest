package spring.ru.springtest.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.ru.springtest.models.CourseModel;

import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<CourseModel, UUID> {

    Page<CourseModel> findAllByIsDeletedFalse(Pageable pageable);

    Optional<CourseModel> findByIdAndIsDeletedFalse(UUID id);

    @Modifying
    @Query(value = """
        INSERT INTO course_student (course_id, student_id)
        VALUES (:courseId, :studentId)
        ON CONFLICT (course_id, student_id) DO NOTHING
        """, nativeQuery = true)
    void insertStudentToCourse(@Param("courseId") UUID courseId,
                               @Param("studentId") UUID studentId);
}
