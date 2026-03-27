package spring.ru.springtest.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import spring.ru.springtest.dto.User;
import spring.ru.springtest.models.ProfileModel;
import spring.ru.springtest.models.UserModel;

@Mapper(componentModel = "spring", uses = ProfileMapper.class)
public interface UserMapper {

    User toDto(UserModel user);

    UserModel toEntity(User dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", ignore = true)
    void updateEntityFromDto(User dto, @MappingTarget UserModel user);

    @AfterMapping
    default void linkProfile(User dto, @MappingTarget UserModel user) {
        if (dto.getProfile() == null) {
            user.setProfile(null);
            return;
        }

        ProfileModel profileModel = user.getProfile();
        if (profileModel == null) {
            profileModel = new ProfileModel();
            profileModel.setBio(dto.getProfile().getBio());
            profileModel.setUser(user);
            user.setProfile(profileModel);
        } else {
            profileModel.setBio(dto.getProfile().getBio());
            profileModel.setUser(user);
        }
    }
}
