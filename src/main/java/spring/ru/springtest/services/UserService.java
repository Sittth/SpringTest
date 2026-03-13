package spring.ru.springtest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.springtest.models.UserModel;
import spring.ru.springtest.repositories.UserRepository;

import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserModel findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void save(UserModel user) {
        if (user.getProfile() != null) {
            user.getProfile().setUser(user);
        }

        userRepository.save(user);
    }

    public void update(UUID id, UserModel updatedUser) {
        updatedUser.setId(id);

        if (updatedUser.getProfile() != null) {
            updatedUser.getProfile().setUser(updatedUser);
        }

        userRepository.save(updatedUser);
    }

    public void delete(UUID id) {
        userRepository.deleteById(id);
    }
}
