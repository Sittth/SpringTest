package spring.ru.springtest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import spring.ru.springtest.dto.create.ProfileCreateRequest;
import spring.ru.springtest.dto.response.ProfileResponse;
import spring.ru.springtest.dto.update.ProfileUpdateRequest;
import spring.ru.springtest.models.ProfileModel;
import spring.ru.springtest.models.UserModel;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProfileMapper {

    ProfileResponse toResponse(ProfileModel profile);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    ProfileModel toEntity(ProfileCreateRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    ProfileModel toEntity(ProfileUpdateRequest dto);

    @Mapping(target = "user", ignore = true)
    void updateEntityFromDto(ProfileUpdateRequest dto, @MappingTarget ProfileModel model);
}
