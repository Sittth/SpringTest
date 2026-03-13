package spring.ru.springtest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.ru.springtest.models.ProfileModel;

import java.util.UUID;

public interface ProfileRepository extends JpaRepository<ProfileModel, UUID> {
}
