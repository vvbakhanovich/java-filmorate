package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmDao {

    Film add(Film film);

    void remove(long id);

    void update(Film film);

    Collection<Film> findAll();

    Film findById(long id);
}
