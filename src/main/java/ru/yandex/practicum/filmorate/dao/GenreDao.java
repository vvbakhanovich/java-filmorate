package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreDao {
    Genre findGenreById(int genreId);

    Collection<Genre> findAll();
}