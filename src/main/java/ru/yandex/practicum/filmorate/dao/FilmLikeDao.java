package ru.yandex.practicum.filmorate.dao;

import java.util.List;

public interface FilmLikeDao {
    void add(long filmId, long userId);

    void update(long filmId, long userId);

    List<Long> getAllById(long filmId);
}
