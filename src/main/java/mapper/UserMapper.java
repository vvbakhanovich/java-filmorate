package mapper;

import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

public final class UserMapper {
    private UserMapper() {

    }

    public static UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
    }

    public static User toModel(UserDto userDto) {
        return new User(userDto.getId(), userDto.getEmail(), userDto.getLogin(), userDto.getName(),
                userDto.getBirthday());
    }
}
