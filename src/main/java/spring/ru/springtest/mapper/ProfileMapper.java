package spring.ru.springtest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import spring.ru.springtest.dto.ProfileRequestCreate;
import spring.ru.springtest.dto.ProfileRequestUpdate;
import spring.ru.springtest.dto.ProfileResponse;
import spring.ru.springtest.models.ProfileModel;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProfileMapper {

    ProfileResponse toResponse(ProfileModel profile);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    ProfileModel toEntity(ProfileRequestCreate dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    ProfileModel toEntity(ProfileRequestUpdate dto);

    @Mapping(target = "user", ignore = true)
    void updateEntityFromDto(ProfileRequestUpdate dto, @MappingTarget ProfileModel model);
}
