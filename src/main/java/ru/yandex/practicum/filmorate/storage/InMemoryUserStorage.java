package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    IdGenerator<Long> idGenerator;

    @Autowired
    public InMemoryUserStorage(IdGenerator<Long> idGenerator) {
        this.idGenerator = idGenerator;
    }

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User add(final User user) {
        user.setId(idGenerator.generateId());
        users.put(user.getId(), user);
        log.info("Сохранен пользователь: {}", user);
        return user;
    }

    @Override
    public boolean remove(final long userId) {
        log.info("Удален пользователь с id {}", userId);
        return users.remove(userId) != null;
    }

    @Override
    public boolean update(final User updatedUser) {
        if (users.containsKey(updatedUser.getId())) {
            users.put(updatedUser.getId(), updatedUser);
            log.info("Обновлен пользователь {}", updatedUser);
            return true;
        }
        return false;
    }

    @Override
    public List<User> findAl() {
        return new ArrayList<>(users.values());
    }
}
