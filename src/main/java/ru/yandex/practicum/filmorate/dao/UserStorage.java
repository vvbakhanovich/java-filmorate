package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage extends Dao<User> {

    Collection<User> findFriendsByUserId(final long userId);
}
