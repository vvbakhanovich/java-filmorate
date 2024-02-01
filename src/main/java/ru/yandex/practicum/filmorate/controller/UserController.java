package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        return userService.addUser(userDto);
    }

    @PutMapping
    public UserDto updateUser(@Valid @RequestBody UserDto updatedUserDto) {
        return userService.updateUser(updatedUserDto);
    }

    @GetMapping
    public Collection<UserDto> getAllUser() {
        return userService.getAllUser();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public UserDto addFriend(@PathVariable long id, @PathVariable long friendId) {
        return userService.addFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<UserDto> showFriendList(@PathVariable long id) {
        return userService.showFriendList(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDto> findCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.findCommonFriends(id, otherId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/recommendations")
    public Collection<FilmDto> showRecommendations(@PathVariable long id) {
        return userService.showRecommendations(id);
    }

}
