package ru.yandex.practicum.filmorate.storage.old;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.IdGenerator;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashMap;
import java.util.Map;

public class InMemoryUserStorage_old implements UserStorage {

    IdGenerator<Long> idGenerator;

    @Autowired
    public InMemoryUserStorage_old(IdGenerator<Long> idGenerator) {
        this.idGenerator = idGenerator;
    }

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public long addUser(final User user) {
        users.put(idGenerator.generateId(), user);
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
