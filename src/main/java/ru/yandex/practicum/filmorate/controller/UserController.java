package ru.yandex.practicum.filmorate.controller;

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
public class UserController {

    private long userId = 1;
    Map<Long, User> users = new HashMap<>();

    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody UserDto userDto) {
        User user = User.build(generateId(), userDto.getEmail(), userDto.getLogin(), userDto.getName(),
                userDto.getBirthday());
        users.put(user.getUserId(), user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@RequestBody UserDto updatedUserDto, @RequestParam long userId) {
        User storedUser = users.get(userId);
        if (storedUser != null) {
            storedUser.setEmail(updatedUserDto.getEmail());
            storedUser.setLogin(updatedUserDto.getLogin());
            storedUser.setName(updatedUserDto.getName());
            storedUser.setBirthday(updatedUserDto.getBirthday());
            return ResponseEntity.ok(storedUser);
        } else {
            throw new UserNotFoundException("Фильм с id " + userId + " не найден.");
        }
    }

    @GetMapping
    public ResponseEntity<ArrayList<User>> getAllUser() {
        return ResponseEntity.ok(new ArrayList<>(users.values()));
    }

    private long generateId() {
        return userId++;
    }
}
