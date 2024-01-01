package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmGenreStorage {
    void add(long filmId, long genreId);

    List<Genre> findAllById(long filmId);

    void deleteAllById(long id);
}
