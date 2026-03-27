package spring.ru.springtest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.models.UserModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel, UUID> {
    List<UserModel> findAllByIsDeletedFalse();

    Optional<UserModel> findByIdAndIsDeletedFalse(UUID id);
}
