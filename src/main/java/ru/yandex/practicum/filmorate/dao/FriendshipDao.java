package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.Collection;
import java.util.Map;

public interface FriendshipDao {
    void merge(Friendship friendship);

    void remove(long userId, long friendId);

    Collection<Friendship> findAll();

    Map<Long, String> findFriendsById(long userId);
}
