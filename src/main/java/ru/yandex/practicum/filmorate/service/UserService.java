package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
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

    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        User user = userStorage.add(UserMapper.toModel(userDto));
        log.info("Добавление нового пользователя: {}", user);
        return UserMapper.toDto(user);
    }

    public UserDto updateUser(@Valid @RequestBody UserDto updatedUserDto) {
        User storedUser = UserMapper.toModel(updatedUserDto);
        long userId = storedUser.getId();
        if (userStorage.update(storedUser)) {
            log.info("Обновление пользователя с id {} : {}", userId, storedUser);
            return UserMapper.toDto(storedUser);
        } else {
            log.error("Пользователь с id {} не был найден.", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
    }

    //TODO заменить на Collection??
    public Collection<UserDto> getAllUser() {
        log.info("Получение списка всех пользователей.");
        return userStorage.findAl().stream().map(UserMapper::toDto).collect(Collectors.toCollection(ArrayList::new));
    }

    private String checkIfNameIsEmpty(UserDto userDto) {
        return userDto.getName() == null || userDto.getName().isBlank() ? userDto.getLogin() : userDto.getName();
    }

}
