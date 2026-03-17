package spring.ru.springtest.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.User;
import spring.ru.springtest.models.UserModel;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final ProfileMapper profileMapper;

    public User toDto(UserModel model) {
        if (model == null) {
            return null;
        }

        return new User().id(model.getId()).username(model.getUsername())
                .profile(profileMapper.toDto(model.getProfile()));
    }

    public UserModel toEntity(User dto) {
        if (dto == null) {
            return null;
        }

        UserModel userModel = new UserModel();
        userModel.setId(dto.getId());
        userModel.setUsername(dto.getUsername());
        userModel.setProfile(profileMapper.toEntity(dto.getProfile()));
        return userModel;
    }
}
