package spring.ru.springtest.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.ProfileRequestCreate;
import spring.ru.springtest.dto.ProfileRequestUpdate;
import spring.ru.springtest.dto.ProfileResponse;
import spring.ru.springtest.models.ProfileModel;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-03T22:08:52+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.1.jar, environment: Java 25 (Oracle Corporation)"
)
@Component
public class ProfileMapperImpl implements ProfileMapper {

    @Override
    public ProfileResponse toResponse(ProfileModel profile) {
        if ( profile == null ) {
            return null;
        }

        ProfileResponse profileResponse = new ProfileResponse();

        profileResponse.setId( profile.getId() );
        profileResponse.setBio( profile.getBio() );

        return profileResponse;
    }

    @Override
    public ProfileModel toEntity(ProfileRequestCreate dto) {
        if ( dto == null ) {
            return null;
        }

        ProfileModel profileModel = new ProfileModel();

        profileModel.setBio( dto.getBio() );

        return profileModel;
    }

    @Override
    public ProfileModel toEntity(ProfileRequestUpdate dto) {
        if ( dto == null ) {
            return null;
        }

        ProfileModel profileModel = new ProfileModel();

        profileModel.setBio( dto.getBio() );

        return profileModel;
    }

    @Override
    public void updateEntityFromDto(ProfileRequestUpdate dto, ProfileModel model) {
        if ( dto == null ) {
            return;
        }

        model.setBio( dto.getBio() );
    }
}
