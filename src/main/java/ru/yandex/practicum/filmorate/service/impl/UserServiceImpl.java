package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.dao.UserDao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    @Qualifier("UserDbStorage")
    private final UserDao userStorage;

    @Override
    public UserDto addUser(final UserDto userDto) {
        User user = UserMapper.toModel(validateUserName(userDto));
        User addedUser = userStorage.add(user);
        log.info("Добавление нового пользователя: {}", addedUser);
        return UserMapper.toDto(addedUser);
    }

    @Override
    public UserDto updateUser(final UserDto updatedUserDto) {
        User updatedUser = UserMapper.toModel(validateUserName(updatedUserDto));
        long userId = updatedUser.getId();
        userStorage.update(updatedUser);
        log.info("Обновление пользователя с id {} : {}", userId, updatedUser);
        return UserMapper.toDto(updatedUser);
    }

    @Override
    public Collection<UserDto> getAllUser() {
        log.info("Получение списка всех пользователей.");
        return userStorage.findAll().stream().map(UserMapper::toDto).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public UserDto getUserById(final long userId) {
        User user = userStorage.findById(userId);
        log.info("Пользователь с id {} найден.", userId);
        return UserMapper.toDto(user);
    }

    @Override
    public UserDto addFriend(final long userId, final long friendId) {
        User user = userStorage.findById(userId);
        user.getFriends().put(friendId, "Не подтверждено.");
        log.info("Пользователи с id {} и id {} стали друзьями.", friendId, userId);
        return UserMapper.toDto(user);

    }

    @Override
    public Collection<UserDto> showFriendList(final long userId) {
        User user = userStorage.findById(userId);
        Set<Long> friendIds = user.getFriends().keySet();
        List<User> result = new ArrayList<>();
        for (Long friendId : friendIds) {
            result.add(userStorage.findById(friendId));
        }
        log.info("Список друзей пользователя с id {}: {}", userId, result);
        return result.stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Collection<UserDto> findCommonFriends(final long userId, final long otherUserId) {
        User user = userStorage.findById(userId);
        User otherUser = userStorage.findById(otherUserId);
        Set<Long> userFriendsId = user.getFriends().keySet();
        Set<Long> otherUserFriendsId = otherUser.getFriends().keySet();
        Set<Long> commonIds = userFriendsId.stream().filter(otherUserFriendsId::contains).collect(Collectors.toSet());
        List<User> result = new ArrayList<>();
        log.info("Cписок id общих друзей пользователей с id {} и {}: {}", userId, otherUser, result);
        for (Long id : commonIds) {
            result.add(userStorage.findById(id));
        }
        return result.stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public UserDto removeFriend(final long userId, final long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);
        Set<Long> userFriendsId = user.getFriends().keySet();
        Set<Long> otherUserFriendsId = friend.getFriends().keySet();
        userFriendsId.remove(friendId);
        otherUserFriendsId.remove(userId);
        log.info("Пользователи с id {} и {} перестали быть друзьями", userId, friend);
        return UserMapper.toDto(user);
    }

    private UserDto validateUserName(final UserDto userDto) {
        String validatedName = userDto.getName() == null || userDto.getName().isBlank() ?
                userDto.getLogin() : userDto.getName();
        userDto.setName(validatedName);
        return userDto;
    }
}