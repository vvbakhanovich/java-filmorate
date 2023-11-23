package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private long userId = 1;
    private final Map<Long, User> users = new HashMap<>();

    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody UserDto userDto) {
        User user = User.build(generateId(), userDto.getEmail(), userDto.getLogin(), checkIfNameIsEmpty(userDto),
                userDto.getBirthday());
        users.put(user.getId(), user);
        log.info("Добавление нового пользователя: " + user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    // Решил задавать id обновляемого фильма в uri, поэтому некоторые тесты для постман, которые были прикреплены к тз
    // не проходят.
    @PutMapping()
    public ResponseEntity<User> updateUser(@Valid @RequestBody UserDto updatedUserDto) {
        long userId = updatedUserDto.getId();
        User storedUser = users.get(userId);
        if (storedUser != null) {
            storedUser.setEmail(updatedUserDto.getEmail());
            storedUser.setLogin(updatedUserDto.getLogin());
            storedUser.setName(checkIfNameIsEmpty(updatedUserDto));
            storedUser.setBirthday(updatedUserDto.getBirthday());
            log.info("Обновление пользователя с id " + userId + ": " + storedUser);
            return ResponseEntity.ok(storedUser);
        } else {
            log.warn("Пользователь с id " + userId + " не был найден.");
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден.");
        }
    }

    @GetMapping
    public ResponseEntity<ArrayList<User>> getAllUser() {
        log.info("Получение списка всех пользователей.");
        return ResponseEntity.ok(new ArrayList<>(users.values()));
    }

    private long generateId() {
        return userId++;
    }

    private String checkIfNameIsEmpty(UserDto userDto) {
        return userDto.getName() == null || userDto.getName().isBlank()  ? userDto.getLogin() : userDto.getName();
    }
}
