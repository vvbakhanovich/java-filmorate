package ru.yandex.practicum.filmorate.dao;

public interface FilmLikeDao {
    void add(long filmId, long userId);

    void update(long filmId, long userId);

    Long getCountById(long filmId);
}
