package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

public final class UserMapper {
    private UserMapper() {

    }

    public static UserDto toDto(User user) {
        UserDto userDto = new UserDto(user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        userDto.getFriends().addAll(user.getFriends());
        return userDto;
    }

    public static User toModel(UserDto userDto) {
        User user = new User(userDto.getId(), userDto.getEmail(), userDto.getLogin(), userDto.getName(),
                userDto.getBirthday());
        user.getFriends().addAll(userDto.getFriends());
        return user;
    }
}
