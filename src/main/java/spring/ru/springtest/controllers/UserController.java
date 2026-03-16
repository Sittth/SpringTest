package spring.ru.springtest.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import spring.ru.springtest.api.UsersApi;
import spring.ru.springtest.dto.User;
import spring.ru.springtest.mapper.UserMapper;
import spring.ru.springtest.models.UserModel;
import spring.ru.springtest.services.UserService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public ResponseEntity<User> usersIdGet(UUID id) {
        UserModel model = userService.findById(id);

        User dto = userMapper.toDto(model);

        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<Void> usersPost(User user) {
        UserModel model = userMapper.toEntity(user);

        userService.save(model);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> usersIdPut(UUID id, User user) {
        UserModel model = userMapper.toEntity(user);

        model.setId(id);
        userService.update(id, model);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> usersIdDelete(UUID id) {
        userService.delete(id);

        return ResponseEntity.ok().build();
    }
}
