package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto addUser(UserDto userDto);

    UserDto updateUser(UserDto userDto);

    Collection<UserDto> getAllUser();

    UserDto getUserById(long userId);

    UserDto addFriend(long userId, long friendId);

    Collection<UserDto> showFriendList(long userId);

    Collection<UserDto> findCommonFriends(long userId, long otherUserId);

    UserDto removeFriend(long userId, long friendId);
}