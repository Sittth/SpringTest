package spring.ru.springtest.mapper;

import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.User;
import spring.ru.springtest.models.UserModel;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-27T20:49:00+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.1.jar, environment: Java 25 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Autowired
    private ProfileMapper profileMapper;

    @Override
    public User toDto(UserModel user) {
        if ( user == null ) {
            return null;
        }

        User user1 = new User();

        user1.setId( user.getId() );
        user1.setUsername( user.getUsername() );
        user1.setProfile( profileMapper.toDto( user.getProfile() ) );

        return user1;
    }

    @Override
    public UserModel toEntity(User dto) {
        if ( dto == null ) {
            return null;
        }

        UserModel userModel = new UserModel();

        userModel.setId( dto.getId() );
        userModel.setUsername( dto.getUsername() );
        userModel.setProfile( profileMapper.toEntity( dto.getProfile() ) );

        linkProfile( dto, userModel );

        return userModel;
    }

    @Override
    public void updateEntityFromDto(User dto, UserModel user) {
        if ( dto == null ) {
            return;
        }

        user.setUsername( dto.getUsername() );

        linkProfile( dto, user );
    }
}
