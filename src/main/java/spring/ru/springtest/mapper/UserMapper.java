package spring.ru.springtest.mapper;

import org.mapstruct.*;
import spring.ru.springtest.dto.create.UserCreateRequest;
import spring.ru.springtest.dto.response.UserResponse;
import spring.ru.springtest.dto.update.UserUpdateRequest;
import spring.ru.springtest.models.UserModel;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = ProfileMapper.class)
public interface UserMapper {

    UserResponse toResponse(UserModel user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", ignore = true)
    UserModel toEntity(UserCreateRequest dto);

    @Mapping(target = "profile", ignore = true)
    void updateEntityFromDto(UserUpdateRequest dto, @MappingTarget UserModel user);
}
