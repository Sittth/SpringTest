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
    public UserResponse getUserById(UUID id) {

        return userService.findById(id);
    }

    @Override
    public GetUsers200Response getUsers(
            @Min(0) @NotNull Integer page,
            @Min(1) @Max(50) @NotNull Integer size) {

        Page<UserResponse> result = userService.findAll(page, size);

        GetUsers200Response response = new GetUsers200Response()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements());

        return response;
    }

    @Override
    public UserResponse createUser(@Valid UserCreateRequest requestCreate) {

        return userService.save(requestCreate);
    }

    @Override
    public UserResponse updateUserById(UUID id, @Valid UserUpdateRequest requestUpdate) {

        return userService.update(id, requestUpdate);
    }

    @Override
    public void deleteUserById(UUID id) {

        userService.delete(id);
    }
}
