package spring.ru.springtest.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import spring.ru.springtest.models.NotificationModel;

import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<NotificationModel, UUID> {
    Optional<NotificationModel> findByIdAndIsDeletedFalse(UUID id);
}
