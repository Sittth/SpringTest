package spring.ru.springtest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import spring.ru.springtest.dto.Profile;
import spring.ru.springtest.models.ProfileModel;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    Profile toDto(ProfileModel profile);

    @Mapping(target = "user", ignore = true)
    ProfileModel toEntity(Profile dto);

    void updateEntityFromDto(Profile dto, @MappingTarget ProfileModel model);
}
