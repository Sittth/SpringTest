package spring.ru.springtest.mapper;

import org.mapstruct.*;
import org.springframework.data.domain.Page;
import spring.ru.springtest.dto.create.UserCreateRequest;
import spring.ru.springtest.dto.response.GetUsers200Response;
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
    @Mapping(target = "deleted", ignore = true)
    UserModel toEntity(UserCreateRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntityFromDto(UserUpdateRequest dto, @MappingTarget UserModel user);

    default ProfileResponse toProfileResponse(ProfileModel profileModel) {
        if (profileModel == null) return null;

        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setId(profileModel.getId());
        profileResponse.setBio(profileModel.getBio());

        return profileResponse;
    }

    default GetUsers200Response toPageResponse(Page<UserResponse> page) {
        return new GetUsers200Response()
            .content(page.getContent())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements());
    }
}
