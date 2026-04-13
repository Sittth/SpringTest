package spring.ru.springtest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.springtest.dto.UserRequestCreate;
import spring.ru.springtest.dto.UserRequestUpdate;
import spring.ru.springtest.dto.UserResponse;
import spring.ru.springtest.exceptions.EntityNotFoundException;
import spring.ru.springtest.mapper.ProfileMapper;
import spring.ru.springtest.mapper.UserMapper;
import spring.ru.springtest.models.UserModel;
import spring.ru.springtest.repositories.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ProfileMapper profileMapper;

    private UserModel findExistingUser(UUID id) {
        return userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("User not found with id {}", id);
                    return new EntityNotFoundException("User " + id);
                });
    }

    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {

        log.debug("Search user by id {}", id);

        UserModel user = findExistingUser(id);

        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse save(UserRequestCreate requestCreate) {

        log.debug("Save user {}", requestCreate);

        UserModel user = userMapper.toEntity(requestCreate);
        UserModel saved = userRepository.save(user);

        log.info("Saved user with id {}", saved.getId());

        return userMapper.toResponse(saved);
    }

    @Transactional
    public UserResponse update(UUID id, UserRequestUpdate requestUpdate) {

        log.debug("Update user with id: {}", id);

        UserModel existingUser = findExistingUser(id);

        userMapper.updateEntityFromDto(requestUpdate, existingUser);
        UserModel saved = userRepository.save(existingUser);

        log.info("Updated user with id {}", id);

        return userMapper.toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {

        log.debug("Delete user with id {}", id);

        UserModel userModel = findExistingUser(id);

        userRepository.delete(userModel);

        log.info("Deleted user with id {}", id);
    }
}
