package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private long userId = 1;
    private final Map<Long, User> users = new HashMap<>();

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        User user = new User(generateId(), userDto.getEmail(), userDto.getLogin(), checkIfNameIsEmpty(userDto),
                userDto.getBirthday());
        users.put(user.getId(), user);
        log.info("Добавление нового пользователя: " + user);
        return UserMapper.toDto(user);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@Valid @RequestBody UserDto updatedUserDto) {
        long userId = updatedUserDto.getId();
        User storedUser = users.get(userId);
        if (storedUser != null) {
            storedUser.setEmail(updatedUserDto.getEmail());
            storedUser.setLogin(updatedUserDto.getLogin());
            storedUser.setName(checkIfNameIsEmpty(updatedUserDto));
            storedUser.setBirthday(updatedUserDto.getBirthday());
            log.info("Обновление пользователя с id " + userId + ": " + storedUser);
            return UserMapper.toDto(storedUser);
        } else {
            log.error("Пользователь с id " + userId + " не был найден.");
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ArrayList<UserDto> getAllUser() {
        log.info("Получение списка всех пользователей.");
        return users.values().stream().map(UserMapper::toDto).collect(Collectors.toCollection(ArrayList::new));
    }

    private long generateId() {
        return userId++;
    }

    private String checkIfNameIsEmpty(UserDto userDto) {
        return userDto.getName() == null || userDto.getName().isBlank() ? userDto.getLogin() : userDto.getName();
    }
}
