package spring.ru.springtest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.springtest.dto.User;
import spring.ru.springtest.mapper.UserMapper;
import spring.ru.springtest.models.UserModel;
import spring.ru.springtest.repositories.UserRepository;

import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public UserModel findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void save(UserModel user) {
        userRepository.save(user);
    }

    public void update(UUID id, User dto) {

        UserModel existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userMapper.updateEntityFromDto(dto, existingUser);

        userRepository.save(existingUser);
    }

    public void delete(UUID id) {
        userRepository.deleteById(id);
    }
}
