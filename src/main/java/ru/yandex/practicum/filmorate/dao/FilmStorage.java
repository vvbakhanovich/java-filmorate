package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage extends Dao<Film> {
    Collection<Film> findMostLikedFilmsLimitBy(int count);
}
