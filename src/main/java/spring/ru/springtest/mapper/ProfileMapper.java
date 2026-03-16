package spring.ru.springtest.mapper;

import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.Profile;
import spring.ru.springtest.models.ProfileModel;

@Component
public class ProfileMapper {

    public Profile toDto(ProfileModel profile) {
        if (profile == null) {
            return null;
        }

        return new Profile().id(profile.getId()).bio(profile.getBio());
    }

    public ProfileModel toEntity(Profile dto) {
        if (dto == null) {
            return null;
        }

        ProfileModel profileModel = new ProfileModel();
        profileModel.setId(dto.getId());
        profileModel.setBio(dto.getBio());
        return profileModel;
    }
}
