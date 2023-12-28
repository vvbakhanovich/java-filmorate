package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserDao {

    User add(User user);

    void remove(long userId);

    void update(User user);

    Collection<User> findAll();

    User findById(long userId);
}
