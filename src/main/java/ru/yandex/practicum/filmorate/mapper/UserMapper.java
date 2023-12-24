package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

@UtilityClass
public class UserMapper {

    public static UserDto toDto(User user) {
        UserDto userDto = new UserDto(user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        userDto.getFriends().putAll(user.getFriends());
        return userDto;
    }

    public static User toModel(UserDto userDto) {
        User user = new User(userDto.getId(), userDto.getEmail(), userDto.getLogin(), userDto.getNickname(),
                userDto.getBirthday());
        user.getFriends().putAll(userDto.getFriends());
        return user;
    }
}
