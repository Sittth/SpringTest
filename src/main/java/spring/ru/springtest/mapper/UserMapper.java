package spring.ru.springtest.mapper;

import org.mapstruct.*;
import spring.ru.springtest.dto.create.UserCreateRequest;
import spring.ru.springtest.dto.response.ProfileResponse;
import spring.ru.springtest.dto.response.UserResponse;
import spring.ru.springtest.dto.update.UserUpdateRequest;
import spring.ru.springtest.models.ProfileModel;
import spring.ru.springtest.models.UserModel;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserResponse toResponse(UserModel user);

    ProfileResponse toProfileResponse(ProfileModel profile);

    @Mapping(target = "id", ignore = true)
    UserModel toEntity(UserCreateRequest dto);

    @Mapping(target = "id", ignore = true)
    ProfileModel toProfileEntity(UserCreateRequest dto);

    void updateEntityFromDto(UserUpdateRequest dto, @MappingTarget UserModel user);

    @AfterMapping
    default void linkProfile(@MappingTarget UserModel user) {
        user.setProfile(user.getProfile());
    }
}
