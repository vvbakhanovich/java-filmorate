package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

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

    private final FriendshipDao friendshipDao;

    @Override
    public UserDto addUser(final UserDto userDto) {
        final User user = UserMapper.toModel(validateUserName(userDto));
        final User addedUser = userStorage.add(user);
        log.info("Добавление нового пользователя: {}", addedUser);
        return UserMapper.toDto(addedUser);
    }

    @Override
    public UserDto updateUser(final UserDto updatedUserDto) {
        final User updatedUser = UserMapper.toModel(validateUserName(updatedUserDto));
        final long userId = updatedUser.getId();
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
        final User user = userStorage.findById(userId);
        log.info("Пользователь с id {} найден.", userId);
        return UserMapper.toDto(user);
    }

    /**
     * При добавлении пользователя в друзья сначала проверяется наличие этих пользователей в БД, после чего происходит
     * добавление в друзья со статусом "Не подтверждено".
     *
     * @param userId   идентификатор пользователя
     * @param friendId идентификатор пользователя, которого требуется добавить в друзья
     * @return пользователь с обновленным списком друзей
     */
    @Override
    public UserDto addFriend(final long userId, final long friendId) {
        final User user = userStorage.findById(userId);
        final User friend = userStorage.findById(friendId);
        Friendship friendship = new Friendship();
        friendship.setId(friendId);
        if (friend.getFriends().stream().map(Friendship::getId).anyMatch(id -> id == userId)) {
            friendship.setStatus("Acknowledged");
            friendshipDao.add(userId, friendId, 1);
            friendshipDao.update(friendId, userId, 1);
        } else {
            friendship.setStatus("Not acknowledged");
            friendshipDao.add(userId, friendId, 2);
        }
        user.getFriends().add(friendship);
        log.info("Пользователи с id {} и id {} стали друзьями.", userId, friendId);
        return UserMapper.toDto(user);
    }

    @Override
    public Collection<UserDto> showFriendList(final long userId) {
//        final User user = userStorage.findById(userId);
//        final List<Friendship> friendships = user.getFriends();
//        final List<User> result = new ArrayList<>();
//        for (Friendship friendship : friendships) {
//            result.add(userStorage.findById(friendship.getId()));
//        }
//        log.info("Список друзей пользователя с id {}: {}", userId, result);
        return userStorage.findFriendsByUserId(userId).stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Collection<UserDto> findCommonFriends(final long userId, final long otherUserId) {
        final User user = userStorage.findById(userId);
        final User otherUser = userStorage.findById(otherUserId);
        final List<Friendship> userFriends = user.getFriends();
        final List<Friendship> otherUserFriends = otherUser.getFriends();
        final List<Long> commonIds = userFriends.stream()
                .map(Friendship::getId)
                .filter(id -> otherUserFriends.stream().anyMatch(friendship1 -> friendship1.getId().equals(id)))
                .collect(Collectors.toList());
        final List<User> result = new ArrayList<>();
        log.info("Получение списка общих друзей пользователей с id {} и {}.", userId, otherUserId);
        for (Long id : commonIds) {
            result.add(userStorage.findById(id));
        }
        return result.stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public UserDto removeFriend(final long userId, final long friendId) {
//        final User user = userStorage.findById(userId);
//        final User friend = userStorage.findById(friendId);
//        friendshipDao.remove(userId, friendId);
//        final Set<Long> userFriendsId = user.getFriends().keySet();
//        userFriendsId.remove(friendId);
//        log.info("Пользователи с id {} и {} перестали быть друзьями", userId, friend);
//        return UserMapper.toDto(user);
        return null;

    }

    private UserDto validateUserName(final UserDto userDto) {
        final String validatedName = userDto.getName() == null || userDto.getName().isBlank() ?
                userDto.getLogin() : userDto.getName();
        userDto.setName(validatedName);
        return userDto;
    }
}