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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    UserModel toEntity(UserCreateRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    ProfileModel toProfileEntity(UserCreateRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    void updateEntityFromDto(UserUpdateRequest dto, @MappingTarget UserModel user);

    default ProfileResponse toProfileResponse(ProfileModel profileModel) {
        if (profileModel == null) return null;

        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setId(profileModel.getId());
        profileResponse.setBio(profileModel.getBio());

        return profileResponse;
    }
}
