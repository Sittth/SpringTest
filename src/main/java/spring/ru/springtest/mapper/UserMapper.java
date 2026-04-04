package spring.ru.springtest.mapper;

import org.mapstruct.*;
import spring.ru.springtest.dto.UserRequestCreate;
import spring.ru.springtest.dto.UserRequestUpdate;
import spring.ru.springtest.dto.UserResponse;
import spring.ru.springtest.models.UserModel;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = ProfileMapper.class)
public interface UserMapper {

    UserResponse toResponse(UserModel user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", ignore = true)
    UserModel toEntity(UserRequestCreate dto);

    @Mapping(target = "profile", ignore = true)
    void updateEntityFromDto(UserRequestUpdate dto, @MappingTarget UserModel user);
}
