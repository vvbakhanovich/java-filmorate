package ru.yandex.practicum.filmorate.dao;

import java.util.Map;
import java.util.Set;

public interface FilmLikeStorage {
    void add(long filmId, long userId);

    Long getCountById(long filmId);

    Map<Long, Long> findAll();

    void remove(long filmId, long userId);
    Set<Long> findLikedFilmsByUser(long userId);
}
