package spring.ru.springtest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.ru.springtest.models.AuthorModel;

import java.util.UUID;

public interface AuthorRepository extends JpaRepository<AuthorModel, UUID> {
}
