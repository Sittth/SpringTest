package spring.ru.springtest.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import spring.ru.springtest.api.UsersApi;
import spring.ru.springtest.dto.*;
import spring.ru.springtest.services.UserService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final UserService userService;

    @Override
    public ResponseEntity<UserResponse> getUserById(UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Override
    public ResponseEntity<UserResponse> createUser(UserRequestCreate requestCreate) {
        UserResponse created = userService.save(requestCreate);

        return ResponseEntity.status(201).body(created);
    }

    @Override
    public ResponseEntity<UserResponse> updateUserById(UUID id, UserRequestUpdate requestUpdate) {
        UserResponse updated = userService.update(id, requestUpdate);

        return ResponseEntity.ok(updated);
    }

    @Override
    public ResponseEntity<Void> deleteUserById(UUID id) {
        userService.delete(id);

        return ResponseEntity.ok().build();
    }
}
