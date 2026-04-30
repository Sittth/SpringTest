package spring.ru.springtest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.springtest.dto.create.UserCreateRequest;
import spring.ru.springtest.dto.response.UserResponse;
import spring.ru.springtest.dto.update.UserUpdateRequest;
import spring.ru.springtest.exceptions.EntityNotFoundException;
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

    private UserModel findExistingUser(UUID id) {
        return userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("User not found with id {}", id);
                    return new EntityNotFoundException("User " + id);
                });
    }

    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {

        log.info("Search user by id {}", id);

        UserModel user = findExistingUser(id);

        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(int page, int size) {

        log.info("Fetching users page {} with size {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));

        Page<UserModel> userPage = userRepository.findAllByIsDeletedFalse(pageable);

        return userPage.map(userMapper::toResponse);
    }

    @Transactional
    public UserResponse save(UserCreateRequest requestCreate) {

        log.info("Save user {}", requestCreate);

        UserModel user = userMapper.toEntity(requestCreate);

        if (user.getProfile() != null) {
            user.setProfile(user.getProfile());
        }

        UserModel saved  = userRepository.save(user);

        log.info("Saved user with id {}", saved.getId());

        return userMapper.toResponse(saved);
    }

    @Transactional
    public UserResponse update(UUID id, UserUpdateRequest requestUpdate) {

        log.info("Update user with id: {}", id);

        UserModel existingUser = findExistingUser(id);

        userMapper.updateEntityFromDto(requestUpdate, existingUser);

        if (requestUpdate.getProfile() != null) {
            if (existingUser == null) {
                existingUser.setProfile(profileMapper.toEntity(requestUpdate.getProfile()));
            } else {
                profileMapper.updateEntityFromDto(
                        requestUpdate.getProfile(),
                        existingUser.getProfile()
                );
            }
        }

        UserModel saved = userRepository.save(existingUser);

        log.info("Updated user with id {}", id);

        return userMapper.toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {

        log.info("Delete user with id {}", id);

        UserModel userModel = findExistingUser(id);

        userRepository.delete(userModel);

        log.info("Deleted user with id {}", id);
    }
}
