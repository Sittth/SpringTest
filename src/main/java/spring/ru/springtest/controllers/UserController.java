package spring.ru.springtest.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import spring.ru.springtest.api.UsersApi;
import spring.ru.springtest.dto.create.UserCreateRequest;
import spring.ru.springtest.dto.response.UserResponse;
import spring.ru.springtest.dto.update.UserUpdateRequest;
import spring.ru.springtest.dto.response.GetUsers200Response;
import spring.ru.springtest.services.UserService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
public class UserController implements UsersApi {

    private final UserService userService;

    @Override
    public ResponseEntity<UserResponse> getUserById(UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Override
    public ResponseEntity<GetUsers200Response> getUsers(
            @Min(0) @NotNull Integer page,
            @Min(1) @Max(50) @NotNull Integer size) {

        Page<UserResponse> result = userService.findAll(page, size);

        GetUsers200Response response = new GetUsers200Response()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UserResponse> createUser(@Valid UserCreateRequest requestCreate) {
        UserResponse created = userService.save(requestCreate);

        return ResponseEntity.status(201).body(created);
    }

    @Override
    public ResponseEntity<UserResponse> updateUserById(UUID id, @Valid UserUpdateRequest requestUpdate) {
        UserResponse updated = userService.update(id, requestUpdate);

        return ResponseEntity.ok(updated);
    }

    @Override
    public ResponseEntity<Void> deleteUserById(UUID id) {
        userService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
