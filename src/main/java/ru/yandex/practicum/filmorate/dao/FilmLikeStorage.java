package ru.yandex.practicum.filmorate.dao;

import java.util.Map;
import java.util.Set;

public interface FilmLikeStorage {
    void add(long filmId, long userId, int rating);

    Long getCountById(long filmId);

    Map<Long, Long> findAll();

    void remove(long filmId, long userId);

    Map<Long, Set<Long>> getUsersAndFilmLikes();
}
