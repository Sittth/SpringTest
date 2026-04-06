package spring.ru.springtest.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import spring.ru.springtest.dto.UserRequestCreate;
import spring.ru.springtest.dto.UserRequestUpdate;
import spring.ru.springtest.dto.UserResponse;
import spring.ru.springtest.models.ProfileModel;
import spring.ru.springtest.models.UserModel;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = ProfileMapper.class)
public abstract class UserMapper {

    @Autowired
    protected  ProfileMapper profileMapper;

    public abstract UserResponse toResponse(UserModel user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", ignore = true)
    public abstract UserModel toEntity(UserRequestCreate dto);

    @Mapping(target = "profile", ignore = true)
    public abstract void updateEntityFromDto(UserRequestUpdate dto, @MappingTarget UserModel user);

    @AfterMapping
    protected void createProfile(@MappingTarget UserModel user, UserRequestCreate dto) {
        if (dto.getProfile() != null) {
            ProfileModel profile = profileMapper.toEntity(dto.getProfile());
            profile.setUser(user);
            user.setProfile(profile);
        }
    }

    @AfterMapping
    protected void updateProfile(@MappingTarget UserModel user, UserRequestUpdate dto) {
        if (dto.getProfile() != null) {
            if (user.getProfile() == null) {
                ProfileModel profile = profileMapper.toEntity(dto.getProfile());
                profile.setUser(user);
                user.setProfile(profile);
            } else {
                profileMapper.updateEntityFromDto(dto.getProfile(), user.getProfile());
                user.getProfile().setUser(user); // синхронизация обратной связи
            }
        }
    }
}
