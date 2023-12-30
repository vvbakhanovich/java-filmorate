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
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.model.FriendshipStatus.ACKNOWLEDGED;
import static ru.yandex.practicum.filmorate.model.FriendshipStatus.NOT_ACKNOWLEDGED;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    @Qualifier("UserDbStorage")
    private final UserDao userStorage;

    private final FriendshipDao friendshipDao;

    /**
     * Сохранение пользователя в БД.
     *
     * @param userDto пользователь
     * @return пользователь с присвоенным идентификатором
     */
    @Override
    public UserDto addUser(final UserDto userDto) {
        final User user = UserMapper.toModel(validateUserName(userDto));
        final User addedUser = userStorage.add(user);
        log.info("Добавление нового пользователя: {}", addedUser);
        return UserMapper.toDto(userStorage.findById(addedUser.getId()));
    }

    /**
     * Обновляются все поля класса за исключением списка друзей.
     *
     * @param updatedUserDto пользователь с новыми данными.
     * @return пользователь с обновленными данными.
     */
    @Override
    public UserDto updateUser(final UserDto updatedUserDto) {
        final User updatedUser = UserMapper.toModel(validateUserName(updatedUserDto));
        final long userId = updatedUser.getId();
        userStorage.update(updatedUser);
        log.info("Обновление пользователя с id {} : {}", userId, updatedUser);
        return UserMapper.toDto(userStorage.findById(userId));
    }

    /**
     * Получение из БД списка всех пользователей. Данные включают список друзей в формате: id - статус дружбы.
     *
     * @return коллекция пользователей.
     */
    @Override
    public Collection<UserDto> getAllUser() {
        log.info("Получение списка всех пользователей.");
        return userStorage.findAll().stream().map(UserMapper::toDto).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Получение пользователя по id.
     *
     * @param userId идентификатор пользователя.
     * @return пользователь, полученный из БД.
     */
    @Override
    public UserDto getUserById(final long userId) {
        final User user = userStorage.findById(userId);
        log.info("Пользователь с id {} найден.", userId);
        return UserMapper.toDto(user);
    }

    /**
     * Добавление пользователя в друзья. Если, пользователи взаимно подружились, то статус дружбы переходит в
     * "Подтверждено".
     *
     * @param userId   идентификатор пользователя.
     * @param friendId идентификатор пользователя, которого требуется добавить в друзья.
     * @return пользователь с обновленным списком друзей.
     */
    @Override
    public UserDto addFriend(final long userId, final long friendId) {
        final User user = userStorage.findById(userId);
        final User friend = userStorage.findById(friendId);
        Friendship friendship = new Friendship();
        friendship.setId(friendId);
        if (friend.getFriends().stream().map(Friendship::getId).anyMatch(id -> id == userId)) {
            friendship.setStatus(ACKNOWLEDGED.getStatus());
            friendshipDao.add(userId, friendId, ACKNOWLEDGED.getId());
            friendshipDao.update(friendId, userId, ACKNOWLEDGED.getId());
        } else {
            friendship.setStatus(NOT_ACKNOWLEDGED.getStatus());
            friendshipDao.add(userId, friendId, NOT_ACKNOWLEDGED.getId());
        }
        user.getFriends().add(friendship);
        log.info("Пользователи с id {} и id {} стали друзьями.", userId, friendId);
        return UserMapper.toDto(userStorage.findById(userId));
    }

    /**
     * Получение списка друзей пользователя.
     *
     * @param userId идентификатор пользователя, список друзей которого требуется отобразить
     * @return список пользователей
     */
    @Override
    public Collection<UserDto> showFriendList(final long userId) {
        log.info("Получение списка друзей пользователя с id {}.", userId);
        return userStorage.findFriendsByUserId(userId).stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    /**
     * Получение списка общих друзей между пользователями.
     *
     * @param userId      идентификатор первого пользователя.
     * @param otherUserId идентификатор второго пользователя.
     * @return список общих друзей между первым и вторым пользователем.
     */
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

    /**
     * Удаление пользователя из списка друзей. Удаление происходит только у пользователя с userId. Если дружба была
     * обоюдной, удаление произойдет только у одного из них.
     *
     * @param userId   идентификатор пользователя.
     * @param friendId идентификатор друга, которого требуется исключить из списка друзей.
     */
    @Override
    public void removeFriend(final long userId, final long friendId) {
        userStorage.findById(userId);
        userStorage.findById(friendId);
        friendshipDao.remove(userId, friendId);
        log.info("Пользователи с id {} и {} перестали быть друзьями", userId, friendId);
    }

    private UserDto validateUserName(final UserDto userDto) {
        final String validatedName = userDto.getName() == null || userDto.getName().isBlank() ?
                userDto.getLogin() : userDto.getName();
        userDto.setName(validatedName);
        return userDto;
    }
}