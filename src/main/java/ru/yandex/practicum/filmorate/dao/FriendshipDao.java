package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.Map;

public interface FriendshipDao {
    void add(long userId, long friendId);

    void remove(long userId, long friendId);

    Map<Long, Friendship> findById(long userId);
}
