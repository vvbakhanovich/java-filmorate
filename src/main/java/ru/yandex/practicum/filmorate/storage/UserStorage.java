package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
    long addUser(User user);

    boolean removeUser(long userId);

    boolean updateUser(User user);
}
