package spring.ru.springtest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StudentRepository extends JpaRepository<StudentRepository, UUID> {
}
