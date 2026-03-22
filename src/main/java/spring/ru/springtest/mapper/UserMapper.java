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
    default void handleProfile(User dto, @MappingTarget UserModel user) {

        if (dto.getProfile() == null) {
            user.setProfile(null);
            return;
        }

        if (user.getProfile() == null) {
            ProfileModel profile = new ProfileModel();
            profile.setBio(dto.getProfile().getBio());
            profile.setUser(user);
            user.setProfile(profile);
        } else {
            ProfileModel existingProfile = user.getProfile();

            if (dto.getProfile().getId() != null) {
                existingProfile.setId(dto.getProfile().getId());
            }

            existingProfile.setBio(dto.getProfile().getBio());
        }
    }
}
