package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public long addUser(final User user) {
        users.put(user.getId(), user);
        return user.getId();
    }

    @Override
    public boolean removeUser(final long user) {
        return users.remove(user) != null;
    }

    @Override
    public boolean updateUser(final User updateUser) {
        if (users.containsKey(updateUser.getId())) {
            users.put(updateUser.getId(), updateUser);
            return true;
        }
        return false;
    }
}
