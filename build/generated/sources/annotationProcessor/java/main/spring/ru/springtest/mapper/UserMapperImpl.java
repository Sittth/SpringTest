package spring.ru.springtest.mapper;

import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.UserRequestCreate;
import spring.ru.springtest.dto.UserRequestUpdate;
import spring.ru.springtest.dto.UserResponse;
import spring.ru.springtest.models.UserModel;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-14T16:20:42+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.1.jar, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Autowired
    private ProfileMapper profileMapper;

    @Override
    public UserResponse toResponse(UserModel user) {
        if ( user == null ) {
            return null;
        }

        UserResponse userResponse = new UserResponse();

        userResponse.setId( user.getId() );
        userResponse.setUsername( user.getUsername() );
        userResponse.setProfile( profileMapper.toResponse( user.getProfile() ) );

        return userResponse;
    }

    @Override
    public UserModel toEntity(UserRequestCreate dto) {
        if ( dto == null ) {
            return null;
        }

        UserModel userModel = new UserModel();

        userModel.setUsername( dto.getUsername() );

        return userModel;
    }

    @Override
    public void updateEntityFromDto(UserRequestUpdate dto, UserModel user) {
        if ( dto == null ) {
            return;
        }

        user.setUsername( dto.getUsername() );
    }
}
