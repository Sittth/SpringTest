package spring.ru.springtest.mapper;

import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.User;
import spring.ru.springtest.models.UserModel;

@Component
public class UserMapper {

    public User toDto(UserModel model) {
        if (model == null) {
            return null;
        }

        return new User().id(model.getId()).username(model.getUsername());
    }

    public UserModel toEntity(User dto) {
        if (dto == null) {
            return null;
        }

        UserModel userModel = new UserModel();
        userModel.setId(dto.getId());
        userModel.setUsername(dto.getUsername());
        return userModel;
    }
}
