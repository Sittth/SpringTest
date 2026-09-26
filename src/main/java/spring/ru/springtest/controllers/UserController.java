package spring.ru.springtest.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import spring.ru.springtest.api.UsersApi;
import spring.ru.springtest.dto.create.UserCreateRequest;
import spring.ru.springtest.dto.response.UserResponse;
import spring.ru.springtest.dto.update.UserUpdateRequest;
import spring.ru.springtest.dto.response.GetUsers200Response;
import spring.ru.springtest.mapper.UserMapper;
import spring.ru.springtest.services.UserService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public UserResponse getUserById(UUID id) {

        return userService.findById(id);
    }

    @Override
    public GetUsers200Response getUsers(Integer page, Integer size) {

        return userMapper.toPageResponse(userService.findAllPaginated(page, size));
    }

    @Override
    public UserResponse createUser(UserCreateRequest requestCreate) {

        return userService.save(requestCreate);
    }

    @Override
    public UserResponse updateUserById(UUID id, UserUpdateRequest requestUpdate) {

        return userService.update(id, requestUpdate);
    }

    @Override
    public void deleteUserById(UUID id) {

        userService.delete(id);
    }
}
