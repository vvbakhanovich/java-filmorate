package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface UserStorage extends Dao<User> {

    Collection<User> findFriendsByUserId(long userId);

    Collection<User> findCommonFriends(long userId, long anotherUserId);

    Map<Long, Set<Long>> showRecommendations();
}
