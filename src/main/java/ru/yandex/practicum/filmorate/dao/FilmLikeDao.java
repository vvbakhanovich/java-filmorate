package ru.yandex.practicum.filmorate.dao;

import java.util.Map;

public interface FilmLikeDao {
    void add(long filmId, long userId);

    void update(long filmId, long userId);

    Long getCountById(long filmId);

    Map<Long, Long> findAll();
}
