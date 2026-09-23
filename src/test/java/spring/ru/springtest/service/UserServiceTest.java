package spring.ru.springtest.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import spring.ru.springtest.controller.AbstractControllerTest;
import spring.ru.springtest.dto.create.ProfileCreateRequest;
import spring.ru.springtest.dto.create.UserCreateRequest;
import spring.ru.springtest.dto.response.UserResponse;
import spring.ru.springtest.dto.update.UserUpdateRequest;
import spring.ru.springtest.exceptions.EntityNotFoundException;
import spring.ru.springtest.models.UserModel;
import spring.ru.springtest.repositories.UserRepository;
import spring.ru.springtest.services.UserService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest extends AbstractControllerTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Test
    void save_shouldPersistUserWithProfile() {
        UserResponse response = userService.save(new UserCreateRequest()
                .username("john_doe")
                .profile(new ProfileCreateRequest("Software Engineer")));

        assertThat(response.getId()).isNotNull();
        assertThat(response.getProfile().getBio()).isEqualTo("Software Engineer");

        // go back through the service (transactional, maps within the session) rather than
        // touching the lazy `profile` association on the raw entity from a non-transactional
        // test method, which would throw LazyInitializationException
        UserResponse reloaded = userService.findById(response.getId());
        assertThat(reloaded.getUsername()).isEqualTo("john_doe");
        assertThat(reloaded.getProfile().getBio()).isEqualTo("Software Engineer");
    }

    @Test
    void findById_shouldThrow_whenUserNotFound() {
        assertThatThrownBy(() -> userService.findById(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void update_shouldChangeUsername() {
        UserResponse created = userService.save(new UserCreateRequest()
                .username("old_name")
                .profile(new ProfileCreateRequest("Old bio")));

        UserResponse updated = userService.update(created.getId(), new UserUpdateRequest().username("new_name"));

        assertThat(updated.getUsername()).isEqualTo("new_name");

        UserModel persisted = userRepository.findByIdAndIsDeletedFalse(created.getId()).orElseThrow();
        assertThat(persisted.getUsername()).isEqualTo("new_name");
    }

    @Test
    void delete_shouldSoftDeleteUser() {
        UserResponse created = userService.save(new UserCreateRequest().username("to_delete"));

        userService.delete(created.getId());

        assertThat(userRepository.findByIdAndIsDeletedFalse(created.getId())).isEmpty();
    }

    @Test
    void findAllPaginated_shouldReturnSavedUsers() {
        userService.save(new UserCreateRequest().username("user_one"));
        userService.save(new UserCreateRequest().username("user_two"));

        var page = userService.findAllPaginated(0, 10);

        assertThat(page.getTotalElements()).isEqualTo(2);
    }
}