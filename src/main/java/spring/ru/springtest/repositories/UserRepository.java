package spring.ru.springtest.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import spring.ru.springtest.models.UserModel;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel, UUID> {
    Page<UserModel> findAllByIsDeletedFalse(Pageable pageable);

    Optional<UserModel> findByIdAndIsDeletedFalse(UUID id);
}
