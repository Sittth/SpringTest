package spring.ru.springtest.service;

import spring.ru.springtest.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spring.ru.springtest.dto.response.UserResponse;
import spring.ru.springtest.mapper.UserMapper;
import spring.ru.springtest.models.UserModel;
import spring.ru.springtest.repositories.UserRepository;
import spring.ru.springtest.services.UserService;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void findById_shouldReturnUser_whenExists() {
        UUID id = UUID.randomUUID();
        UserModel model = new UserModel();
        model.setId(id);
        UserResponse expected = new UserResponse().id(id);

        when(userRepository.findByIdAndIsDeletedFalse(id)).thenReturn(Optional.of(model));
        when(userMapper.toResponse(model)).thenReturn(expected);

        assertThat(userService.findById(id)).isEqualTo(expected);
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findByIdAndIsDeletedFalse(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void update_shouldUseForUpdateLookup_notPlainFindById() {
        UUID id = UUID.randomUUID();
        UserModel existing = new UserModel();
        existing.setId(id);
        var request = new spring.ru.springtest.dto.update.UserUpdateRequest().username("new_name");

        when(userRepository.findByIdAndIsDeletedFalseForUpdate(id)).thenReturn(Optional.of(existing));
        when(userMapper.toResponse(existing)).thenReturn(new UserResponse().id(id));

        userService.update(id, request);

        verify(userMapper).updateEntityFromDto(request, existing);
        verify(userRepository, never()).findByIdAndIsDeletedFalse(id);
        verify(userRepository).findByIdAndIsDeletedFalseForUpdate(id);
    }

    @Test
    void update_shouldThrow_whenUserNotFoundForUpdate() {
        UUID id = UUID.randomUUID();
        when(userRepository.findByIdAndIsDeletedFalseForUpdate(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(id, new spring.ru.springtest.dto.update.UserUpdateRequest()))
                .isInstanceOf(EntityNotFoundException.class);

        verifyNoInteractions(userMapper);
    }

    @Test
    void delete_shouldRemoveUser() {
        UUID id = UUID.randomUUID();
        UserModel model = new UserModel();
        model.setId(id);
        when(userRepository.findByIdAndIsDeletedFalseForUpdate(id)).thenReturn(Optional.of(model));

        userService.delete(id);

        verify(userRepository).delete(model);
    }

    @Test
    void save_shouldPersistAndReturnResponse() {
        var request = new spring.ru.springtest.dto.create.UserCreateRequest().username("john_doe");
        UserModel entity = new UserModel();
        UserModel saved = new UserModel();
        saved.setId(UUID.randomUUID());
        UserResponse expected = new UserResponse().id(saved.getId());

        when(userMapper.toEntity(request)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(saved);
        when(userMapper.toResponse(saved)).thenReturn(expected);

        assertThat(userService.save(request)).isEqualTo(expected);
    }
}
