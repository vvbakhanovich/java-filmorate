package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage extends Dao<User> {

    Collection<User> findFriendsByUserId(long userId);
    Collection<User> findCommonFriends(long userId, long anotherUserId);
    Collection<Feed> getFeed(long id);
}
