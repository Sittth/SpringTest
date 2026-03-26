package spring.ru.springtest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.springtest.dto.User;
import spring.ru.springtest.exceptions.UserNotFoundException;
import spring.ru.springtest.mapper.UserMapper;
import spring.ru.springtest.models.AuthorModel;
import spring.ru.springtest.models.UserModel;
import spring.ru.springtest.repositories.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public User findById(UUID id) {

        log.debug("Search user by id {}", id);

        UserModel userModel = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with id {}", id);
                    return new UserNotFoundException("User with id " + id + " not found");
                });
        return userMapper.toDto(userModel);
    }

    @Transactional
    public void save(User userDto) {

        log.debug("Save user {}", userDto);

        UserModel userModel = userMapper.toEntity(userDto);
        userRepository.save(userModel);

        log.info("Saved user with id {}", userDto.getId());
    }

    @Transactional
    public void update(UUID id, User dto) {

        log.debug("Update user with id: {}", id);

        UserModel existingUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Update failed: user not found with id {}", id);
                    return new UserNotFoundException("User with id " + id + " not found");
                });
        userMapper.updateEntityFromDto(dto, existingUser);
        userRepository.save(existingUser);

        log.info("Updated user with id {}", id);
    }

    @Transactional
    public void delete(UUID id) {

        log.debug("Delete user with id {}", id);

        if (!userRepository.existsById(id)) {
            log.warn("Attempt to delete non-existent user with id: {}", id);
            throw new UserNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);

        log.info("Deleted user with id {}", id);
    }
}
