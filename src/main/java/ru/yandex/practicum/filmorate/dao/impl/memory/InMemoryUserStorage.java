package ru.yandex.practicum.filmorate.dao.impl.memory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.IdGenerator;
import ru.yandex.practicum.filmorate.dao.UserStorage;

import java.util.*;

@Repository
@Qualifier("inMemoryUserStorage")
@Slf4j
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    private final IdGenerator<Long> idGenerator;

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User add(final User user) {
        user.setId(idGenerator.generateId());
        users.put(user.getId(), user);
        log.info("Сохранен пользователь: {}", user);
        return user;
    }

    @Override
    public void remove(final long userId) {
        if (users.containsKey(userId)) {
            users.remove(userId);
            log.info("Удален пользователь с id {}", userId);
        } else {
            log.error("Пользователь с id {} не был найден.", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
    }

    @Override
    public void update(final User updatedUser) {
        long userId = updatedUser.getId();
        if (users.containsKey(userId)) {
            users.put(updatedUser.getId(), updatedUser);
            log.info("Обновлен пользователь {}", updatedUser);
        } else {
            log.error("Пользователь с id {} не был найден.", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(long userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            log.error("Пользователь с userId {} не был найден.", userId);
            throw new NotFoundException("Пользователь с userId " + userId + " не найден.");
        }
    }

    @Override
    public Collection<User> findFriendsByUserId(long userId) {
        return null;
    }
}
