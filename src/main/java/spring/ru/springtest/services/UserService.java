package spring.ru.springtest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.springtest.dto.UserRequestCreate;
import spring.ru.springtest.dto.UserRequestUpdate;
import spring.ru.springtest.dto.UserResponse;
import spring.ru.springtest.exceptions.UserNotFoundException;
import spring.ru.springtest.mapper.ProfileMapper;
import spring.ru.springtest.mapper.UserMapper;
import spring.ru.springtest.models.ProfileModel;
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

    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {

        log.debug("Search user by id {}", id);

        UserModel user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("User not found with id {}", id);
                    return new UserNotFoundException("User with id " + id + " not found");
                });
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse save(UserRequestCreate requestCreate) {

        log.debug("Save user {}", requestCreate);

        UserModel user = userMapper.toEntity(requestCreate);

        if (requestCreate.getProfile() != null) {
            ProfileModel profile = profileMapper.toEntity(requestCreate.getProfile());
            profile.setUser(user);
            user.setProfile(profile);
        }

        UserModel saved = userRepository.save(user);

        log.info("Saved user with id {}", saved.getId());

        return userMapper.toResponse(saved);
    }

    @Transactional
    public UserResponse update(UUID id, UserRequestUpdate requestUpdate) {

        log.debug("Update user with id: {}", id);

        UserModel existingUser = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Update failed: user not found with id {}", id);
                    return new UserNotFoundException("User with id " + id + " not found");
                });
        userMapper.updateEntityFromDto(requestUpdate, existingUser);

        if (requestUpdate.getProfile() != null) {
            ProfileModel profile = existingUser.getProfile();

            if (profile == null) {
                profile = profileMapper.toEntity(requestUpdate.getProfile());
                profile.setUser(existingUser);
                existingUser.setProfile(profile);
            } else {
                profileMapper.updateEntityFromDto(requestUpdate.getProfile(), profile);
                profile.setUser(existingUser);
            }
        }

        UserModel saved = userRepository.save(existingUser);

        log.info("Updated user with id {}", id);

        return userMapper.toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {

        log.debug("Delete user with id {}", id);

        UserModel userModel = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.warn("Attempt to delete non-existent or already deleted user with id: {}", id);
                    return new UserNotFoundException("User with id " + id + " not found");
                });

        userRepository.delete(userModel);

        log.info("Deleted user with id {}", id);
    }
}
