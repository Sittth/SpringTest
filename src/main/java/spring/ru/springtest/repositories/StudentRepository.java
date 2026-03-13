package spring.ru.springtest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.ru.springtest.models.StudentModel;

import java.util.UUID;

public interface StudentRepository extends JpaRepository<StudentModel, UUID> {
}
