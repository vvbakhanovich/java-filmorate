package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Repository
@Slf4j
public class InMemoryUserStorage implements Storage<User> {

    IdGenerator<Long> idGenerator;

    @Autowired
    public InMemoryUserStorage(IdGenerator<Long> idGenerator) {
        this.idGenerator = idGenerator;
    }

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public long add(final User user) {
        users.put(idGenerator.generateId(), user);
        return user.getId();
    }

    @Override
    public boolean remove(final long user) {
        return users.remove(user) != null;
    }

    @Override
    public boolean update(final User updateUser) {
        if (users.containsKey(updateUser.getId())) {
            users.put(updateUser.getId(), updateUser);
            return true;
        }
        return false;
    }
}
