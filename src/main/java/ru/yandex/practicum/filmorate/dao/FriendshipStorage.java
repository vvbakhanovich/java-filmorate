package ru.yandex.practicum.filmorate.dao;

public interface FriendshipStorage {
    void add(long userId, long friendId, int statusId);

    void remove(long userId, long friendId);

    void update(long userId, long friendId, int statusId);
}
