package spring.ru.springtest.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.Profile;
import spring.ru.springtest.models.ProfileModel;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-24T18:29:47+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.1.jar, environment: Java 25 (Oracle Corporation)"
)
@Component
public class ProfileMapperImpl implements ProfileMapper {

    @Override
    public Profile toDto(ProfileModel profile) {
        if ( profile == null ) {
            return null;
        }

        Profile profile1 = new Profile();

        profile1.setId( profile.getId() );
        profile1.setBio( profile.getBio() );

        return profile1;
    }

    @Override
    public ProfileModel toEntity(Profile dto) {
        if ( dto == null ) {
            return null;
        }

        ProfileModel profileModel = new ProfileModel();

        profileModel.setId( dto.getId() );
        profileModel.setBio( dto.getBio() );

        return profileModel;
    }

    @Override
    public void updateEntityFromDto(Profile dto, ProfileModel model) {
        if ( dto == null ) {
            return;
        }

        model.setId( dto.getId() );
        model.setBio( dto.getBio() );
    }
}
