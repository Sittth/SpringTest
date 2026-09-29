package spring.ru.springtest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.ru.springtest.models.NotificationOutboxModel;

import java.util.UUID;

public interface NotificationOutboxRepository extends JpaRepository<NotificationOutboxModel, UUID> {
}
