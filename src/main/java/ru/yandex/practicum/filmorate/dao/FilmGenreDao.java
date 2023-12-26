package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmGenreDao {
    void add(long filmId, long genreId);

    void update(long filmId, long genreId);

    List<Genre> getAllById(long filmId);
}
