package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {
    long addFilm(Film film);
    boolean removeFilm(long filmId);
    boolean updateFilm(Film film);
}