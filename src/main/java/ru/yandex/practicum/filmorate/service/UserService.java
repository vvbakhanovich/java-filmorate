package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto addUser(final UserDto userDto) {
        User user = UserMapper.toModel(validateUserName(userDto));
        User addedUser = userStorage.add(user);
        log.info("Добавление нового пользователя: {}", addedUser);
        return UserMapper.toDto(addedUser);
    }

    public UserDto updateUser(final UserDto updatedUserDto) {
        User updatedUser = UserMapper.toModel(validateUserName(updatedUserDto));
        long userId = updatedUser.getId();
        if (userStorage.update(updatedUser)) {
            log.info("Обновление пользователя с id {} : {}", userId, updatedUser);
            return UserMapper.toDto(updatedUser);
        } else {
            log.error("Пользователь с id {} не был найден.", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
    }

    public Collection<UserDto> getAllUser() {
        log.info("Получение списка всех пользователей.");
        return userStorage.findAll().stream().map(UserMapper::toDto).collect(Collectors.toCollection(ArrayList::new));
    }

    public UserDto addFriend(final Long userId, long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);
        if (user != null) {
            if (friend != null) {
                user.getFriends().add(friendId);
                userStorage.update(user);
                friend.getFriends().add(userId);
                userStorage.update(friend);
                log.info("Пользователи с id {} и id {} стали друзьями.", friendId, userId);
                return UserMapper.toDto(user);
            } else {
                log.error("Пользователь с id {} не был найден.", friendId);
                throw new NotFoundException("Пользователь id " + friendId + " не найден");
            }
        } else {
            log.error("Пользователь с id {} не был найден.", userId);
            throw new NotFoundException("Пользователь id " + userId + " не найден");
        }
    }


    private UserDto validateUserName(final UserDto userDto) {
        String validatedName = userDto.getName() == null || userDto.getName().isBlank() ?
                userDto.getLogin() : userDto.getName();
        userDto.setName(validatedName);
        return userDto;
    }
}
