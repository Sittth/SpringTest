package spring.ru.springtest.repositories;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.ru.springtest.models.UserModel;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select u
        from UserModel u
        where u.id = :id
        and u.isDeleted = false
    """)
    Optional<UserModel> findByIdAndIsDeletedFalseForUpdate(@Param("id") UUID id);

    Page<UserModel> findAllByIsDeletedFalse(Pageable pageable);

    Optional<UserModel> findByIdAndIsDeletedFalse(UUID id);
}
